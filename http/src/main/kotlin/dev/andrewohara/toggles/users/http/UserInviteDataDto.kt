package dev.andrewohara.toggles.users.http

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.http.togglesJson

data class UserInviteDataDto(
    val emailAddress: EmailAddress,
    val permissions: UserPermissionsDataDto
) {
    companion object {
        val lens = togglesJson.autoBody<UserInviteDataDto>().toLens()
        val sample = UserInviteDataDto(
            emailAddress = EmailAddress.parse("user@domain.com"),
            permissions = UserPermissionsDataDto.sample
        )
    }
}