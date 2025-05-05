package dev.andrewohara.toggles

import dev.andrewohara.toggles.apikeys.generateApiKey
import dev.andrewohara.toggles.http.ProjectCreateDataDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.toggles.projects.Project
import dev.andrewohara.toggles.projects.ProjectCreateData
import dev.andrewohara.toggles.projects.createProject
import dev.andrewohara.toggles.projects.listProjects
import dev.andrewohara.toggles.tenants.Tenant
import dev.andrewohara.toggles.toggles.createToggle
import dev.andrewohara.toggles.toggles.deleteToggle
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class ProjectsHttpContract: ServerContractBase() {

    private lateinit var tenant1: Tenant

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = Tenant(TenantId.random(random), TenantName.of("tenant1"), time)
            .also(storage.tenants::plusAssign)
    }

    @Test
    fun `create project - success`() {
        val expected = Project(tenant1.tenantId, projectName1, time, time, devAndProd)

        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)) shouldBeSuccess expected

        httpClient.listProjects(null) shouldBeSuccess ProjectsPageDto(
            items = listOf(expected.toDto()),
            next = null
        )
    }
    
    @Test
    fun `create project - already exists`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient.createProject(ProjectCreateDataDto(projectName1, devAndProd)) shouldBeFailure TogglesErrorDto(
            message = "Project already exists: $projectName1"
        )
    }

    @Test
    fun `list projects`() {
        val project1 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val project2 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName2, devAndProd)).shouldBeSuccess()
        val project3 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName3, devAndProd)).shouldBeSuccess()

        val page1 = httpClient.listProjects(null).shouldBeSuccess()
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = httpClient.listProjects(page1.next).shouldBeSuccess()
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(
            project1.toDto(), project2.toDto(), project3.toDto()
        )
    }

    @Test
    fun `delete project - not found`() {
        httpClient.deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `delete project - success`() {
        val project1 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()
        toggles.deleteToggle(tenant1.tenantId, projectName1, toggleName1).shouldBeSuccess()

        httpClient.deleteProject(projectName1) shouldBeSuccess project1.toDto()
        toggles.listProjects(tenant1.tenantId, null) shouldBe Page(
            items = emptyList(),
            next = null
        )
    }

    @Test
    fun `delete project - still has toggles`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient.deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not empty: $projectName1"
        )
    }

    @Test
    fun `delete project - still has api keys`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.generateApiKey(tenant1.tenantId, projectName1, dev).shouldBeSuccess()

        httpClient.deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not empty: $projectName1"
        )
    }
}

class InMemoryProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = Storage.inMemory()
}