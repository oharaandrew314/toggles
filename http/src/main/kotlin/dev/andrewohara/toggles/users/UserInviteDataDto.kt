package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.togglesJson

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