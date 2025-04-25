package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.ToggleName

data class TogglesPageDto(
    val items: List<ToggleDto>,
    val next: ToggleName?
) {
    companion object {
        val lens = togglesJson.autoBody<TogglesPageDto>().toLens()
        val sample = TogglesPageDto(
            items = listOf(ToggleDto.sample),
            next = ToggleName.of("my_toggle")
        )
    }
}