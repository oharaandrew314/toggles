package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.TenantId
import java.time.Instant

data class Tenant(
    val tenantId: TenantId,
    val createdOn: Instant
)