package dev.andrewohara.toggles.users.http

import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.http.togglesJson

data class UserPageDto(
    val items: List<UserDto>,
    val next: UniqueId?
) {
    companion object {
        val lens = togglesJson.autoBody<UserPageDto>().toLens()
        val sample = UserPageDto(
            items = listOf(UserDto.sample),
            next = UniqueId.parse("nextPage")
        )
    }
}