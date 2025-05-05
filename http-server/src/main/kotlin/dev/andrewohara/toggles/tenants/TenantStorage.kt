package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TenantNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface TenantStorage {
    fun list(pageSize: Int): Paginator<Tenant, TenantId>

    operator fun get(tenantId: TenantId): Tenant?
    operator fun plusAssign(tenant: Tenant)
    operator fun minusAssign(tenant: Tenant)

    fun getOrFail(tenantId: TenantId) = get(tenantId).asResultOr { TenantNotFound(tenantId) }
}