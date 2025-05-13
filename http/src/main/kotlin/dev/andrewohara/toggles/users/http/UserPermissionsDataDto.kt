package dev.andrewohara.toggles.users.http

import dev.andrewohara.toggles.http.togglesJson

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