package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.togglesJson
import java.time.Instant

data class UserDto(
    val userId: UniqueId,
    val emailAddress: EmailAddress,
    val createdOn: Instant,
    val role: UserRoleDto
) {
    companion object {
        val lens = togglesJson.autoBody<UserDto>().toLens()
        val sample = UserDto(
            userId = UniqueId.parse("ABCDEFGH"),
            emailAddress = EmailAddress.parse("user@domain.com"),
            createdOn = Instant.parse("2025-04-24T12:00:00Z"),
            role = UserRoleDto.Developer
        )
    }
}

enum class UserRoleDto { Admin, Developer, Tester }