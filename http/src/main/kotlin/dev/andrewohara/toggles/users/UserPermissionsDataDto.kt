package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.togglesJson

data class UserPermissionsDataDto(
    val role: UserRoleDto
) {
    companion object {
        val lens = togglesJson.autoBody<UserPermissionsDataDto>().toLens()
        val sample = UserPermissionsDataDto(
            role = UserRoleDto.Tester
        )
    }
}