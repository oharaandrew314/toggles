package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.togglesJson
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class TogglesPageDto(
    val items: List<ToggleDto>,
    val next: ToggleName?
) {
    companion object {
        val lens = togglesJson.autoBody<TogglesPageDto>().toLens()
        val sample = TogglesPageDto(
            items = listOf(ToggleDto.Companion.sample),
            next = ToggleName.Companion.of("my_toggle")
        )
    }
}