package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantName

data class TenantCreateData(
    val ownerEmailAddress: EmailAddress,
    val tenantName: TenantName
)