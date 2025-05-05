package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TenantName
import java.time.Instant

data class Tenant(
    val tenantId: TenantId,
    val tenantName: TenantName,
    val createdOn: Instant
)