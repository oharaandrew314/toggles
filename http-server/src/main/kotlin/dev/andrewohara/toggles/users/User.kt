package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UniqueId
import java.time.Instant

data class User(
    val tenantId: TenantId,
    val uniqueId: UniqueId,
    val emailAddress: EmailAddress,
    val createdOn: Instant,
    val role: UserRole
)

enum class UserRole { Admin, Developer, Tester }