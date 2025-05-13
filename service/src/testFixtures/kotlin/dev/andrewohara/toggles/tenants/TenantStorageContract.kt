package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.StorageContractBase
import dev.andrewohara.toggles.TenantId
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class TenantStorageContract: StorageContractBase() {

    private lateinit var tenant1: Tenant
    private lateinit var tenant2: Tenant
    private lateinit var tenant3: Tenant

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = Tenant(TenantId.Companion.random(random), time).also(storage.tenants::plusAssign)
        tenant2 = Tenant(TenantId.Companion.random(random), time).also(storage.tenants::plusAssign)
        tenant3 = Tenant(TenantId.Companion.random(random), time).also(storage.tenants::plusAssign)
    }

    @Test
    fun `list tenants - all`() {
        storage.tenants.list(pageSize = 2).toList().shouldContainExactlyInAnyOrder(tenant1, tenant2, tenant3)
    }

    @Test
    fun `list tenants - paged`() {
        val page1 = storage.tenants.list(pageSize = 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.tenants.list(pageSize = 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()
    }

    @Test
    fun `get tenant - found`() {
        storage.tenants[tenant1.tenantId] shouldBe tenant1
    }

    @Test
    fun `get tenant - not found`() {
        storage.tenants[TenantId.Companion.random(random)] shouldBe null
    }

    @Test
    fun `delete tenant - found`() {
        storage.tenants -= tenant1
        storage.tenants[tenant1.tenantId].shouldBeNull()
    }

    @Test
    fun `delete tenant - not found`() {
        storage.tenants -= tenant1
        storage.tenants -= tenant1
    }
}