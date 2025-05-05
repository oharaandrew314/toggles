package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UserId

data class User(
    val tenantId: TenantId,
    val userId: UserId,
    val emailAddress: EmailAddress,
    val role: UserRole
)

enum class UserRole { Admin, Developer, Tester }