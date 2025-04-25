package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight

data class ToggleCreateDataDto(
    val toggleName: ToggleName,
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>,
    val defaultVariation: VariationName
) {
    companion object {
        val lens = togglesJson.autoBody<ToggleCreateDataDto>().toLens()

        val sample = ToggleCreateDataDto(
            toggleName = ToggleName.of("my_toggle"),
            variations = ToggleUpdateDataDto.sample.variations,
            defaultVariation = ToggleUpdateDataDto.sample.defaultVariation,
            overrides = ToggleUpdateDataDto.sample.overrides,
        )
    }
}