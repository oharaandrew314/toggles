package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.*
import dev.andrewohara.toggles.tenants.Tenant
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

abstract class ProjectStorageContract: StorageContractBase() {

    private lateinit var tenant1: Tenant

    private lateinit var project1: Project
    private lateinit var project2: Project
    private lateinit var project3: Project

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = Tenant(TenantId.Companion.random(random), time)
            .also(storage.tenants::plusAssign)

        project1 = Project(tenant1.tenantId, projectName1, time, time, devAndProd)
            .also(storage.projects::plusAssign)
        project2 = Project(tenant1.tenantId,
            projectName2, time + Duration.ofMinutes(1), time + Duration.ofMinutes(1),
            devAndProd
        )
            .also(storage.projects::plusAssign)
        project3 = Project(tenant1.tenantId,
            projectName3, time + Duration.ofMinutes(2), time + Duration.ofMinutes(2),
            devAndProd
        )
            .also(storage.projects::plusAssign)

        storage.projects.list(tenant1.tenantId, 100)
            .toList()
            .shouldHaveSize(3)
            .shouldContainExactlyInAnyOrder(project1, project2, project3)
    }

    @Test
    fun `list project - paged`() {
        val page1 = storage.projects.list(tenant1.tenantId, 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.projects.list(tenant1.tenantId, 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(project1, project2, project3)
    }

    @Test
    fun `get storage - found`() {
        storage.projects[tenant1.tenantId, projectName2] shouldBe project2
    }

    @Test
    fun `get storage - not found`() {
        storage.projects[tenant1.tenantId, ProjectName.Companion.of("missing")] shouldBe null
    }

    @Test
    fun `delete - success`() {
        storage.projects -= project2

        storage.projects.list(tenant1.tenantId, 100)
            .toList()
            .shouldContainExactlyInAnyOrder(project1, project3)
    }

    @Test
    fun `delete - not found`() {
        storage.projects -= project2
        storage.projects -= project2
    }

    @Test
    fun `save - can update`() {
        val updated = project1.copy(
            createdOn = project1.createdOn.plusSeconds(30),
            environments = listOf(dev, staging, prod)
        )

        storage.projects += updated

        storage.projects.list(tenant1.tenantId, 100).toList().shouldContainExactlyInAnyOrder(updated, project2, project3)
    }
}