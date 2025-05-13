package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.*
import dev.andrewohara.toggles.projects.Project
import dev.andrewohara.toggles.tenants.Tenant
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class ToggleStorageContract: StorageContractBase() {

    private lateinit var tenant1: Tenant

    private lateinit var toggle1: Toggle
    private lateinit var toggle2: Toggle
    private lateinit var toggle3: Toggle
    private lateinit var toggle4: Toggle

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = Tenant(TenantId.Companion.random(random), time)
            .also(storage.tenants::plusAssign)

        storage.projects += Project(tenant1.tenantId, projectName1, time, time, devAndProd)
        storage.projects += Project(tenant1.tenantId, projectName2, time, time, devAndProd)

        toggle1 = oldNewData
            .toCreate(toggleName1)
            .toToggle(tenant1.tenantId, projectName1, time)
            .also(storage.toggles::plusAssign)

        toggle2 = onOffData
            .toCreate(toggleName2)
            .toToggle(tenant1.tenantId, projectName1, time.plusSeconds(60))
            .also(storage.toggles::plusAssign)

        toggle3 = oldNewData
            .toCreate(toggleName3)
            .toToggle(tenant1.tenantId, projectName1, time.plusSeconds(120))
            .also(storage.toggles::plusAssign)

        toggle4 = ToggleUpdateData(
            variations = listOf(on, off),
            defaultVariation = off,
            environments = emptyMap()
        )
            .toCreate(toggleName1)
            .toToggle(tenant1.tenantId, projectName2, time)
            .also(storage.toggles::plusAssign)
    }

    @Test
    fun `list toggles - all`() {
        storage.toggles.list(tenant1.tenantId, projectName1, pageSize = 2)
            .toList()
            .shouldContainExactlyInAnyOrder(toggle1, toggle2, toggle3)

    }

    @Test
    fun `list toggles - paged`() {
        val page1 = storage.toggles.list(tenant1.tenantId, projectName1, pageSize = 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.toggles.list(tenant1.tenantId, projectName1, pageSize = 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(toggle1, toggle2, toggle3)
    }

    @Test
    fun `get toggle - found`() {
        storage.toggles[tenant1.tenantId, projectName1, toggleName1] shouldBe toggle1
    }

    @Test
    fun `get toggle - empty environments`() {
        storage.toggles[tenant1.tenantId, projectName2, toggleName1] shouldBe toggle4
    }

    @Test
    fun `get toggle - not found`() {
        storage.toggles[tenant1.tenantId, projectName2, toggleName2].shouldBeNull()
    }

    @Test
    fun `delete toggle - found`() {
        storage.toggles -= toggle1

        storage.toggles.list(tenant1.tenantId, projectName1, 100)
            .toList()
            .shouldContainExactlyInAnyOrder(toggle2, toggle3)
    }

    @Test
    fun `delete toggle - not found`() {
        storage.toggles -= toggle2
        storage.toggles -= toggle2
    }

    @Test
    fun `save - can update`() {
        val updated = toggle1.copy(
            defaultVariation = new
        )

        storage.toggles += updated
        storage.toggles[tenant1.tenantId, toggle1.projectName, toggle1.toggleName] shouldBe updated
    }
}