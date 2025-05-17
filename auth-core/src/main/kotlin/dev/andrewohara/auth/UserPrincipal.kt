package dev.andrewohara.auth

import dev.andrewohara.toggles.EmailAddress
import java.time.Instant

data class UserPrincipal(
    val emailAddress: EmailAddress,
    val name: String,
    val photoUrl: String?,
    val expires: Instant
)