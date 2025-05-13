package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.EmailAddress

data class TenantCreateData(
    val ownerEmailAddress: EmailAddress,
)