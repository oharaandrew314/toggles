package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.togglesJson
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ToggleCreateDataDto(
    val toggleName: ToggleName,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironmentDto>
) {
    companion object {
        val lens = togglesJson.autoBody<ToggleCreateDataDto>().toLens()

        val sample = ToggleCreateDataDto(
            toggleName = ToggleName.of("my_toggle"),
            variations = ToggleUpdateDataDto.sample.variations,
            defaultVariation = ToggleUpdateDataDto.sample.defaultVariation,
            environments = ToggleUpdateDataDto.sample.environments
        )
    }
}