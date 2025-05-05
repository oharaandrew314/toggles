package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

fun inMemoryTenantStorage() = object: TenantStorage {

    private val comparator = Comparator<Tenant> { o1, o2 -> o1.tenantId.compareTo(o2.tenantId) }

    private val tenants = ConcurrentSkipListSet(comparator)

    override fun list(pageSize: Int) = Paginator<Tenant, TenantId> { cursor ->
        val page = tenants
            .sortedWith(comparator)
            .dropWhile { cursor != null && it.tenantId <= cursor }
            .take(pageSize + 1)

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.tenantId
        )
    }

    override fun get(tenantId: TenantId) = tenants.find { it.tenantId == tenantId }

    override fun plusAssign(tenant: Tenant) = tenants.plusAssign(tenant)

    override fun minusAssign(tenant: Tenant)  = tenants.minusAssign(tenant)
}