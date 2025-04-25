package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight

data class ToggleUpdateDataDto(
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>,
    val defaultVariation: VariationName
) {
    companion object {
        val lens = togglesJson.autoBody<ToggleUpdateDataDto>().toLens()

        val sample = ToggleUpdateDataDto(
            variations = mapOf(
                VariationName.of("off") to Weight.of(2),
                VariationName.of("on") to Weight.of(1)
            ),
            defaultVariation = VariationName.of("off"),
            overrides = mapOf(
                SubjectId.of("user1") to VariationName.of("on")
            )
        )
    }
}