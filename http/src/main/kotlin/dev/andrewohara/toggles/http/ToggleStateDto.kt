package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ToggleStateDto(
    val uniqueId: UniqueId,
    val variations: Map<VariationName, Weight>,
    val defaultVariation: VariationName,
    val overrides: Map<SubjectId, VariationName>
) {
    companion object {
        val lens = togglesJson.autoBody<ToggleStateDto>().toLens()
        val sample = ToggleStateDto(
            uniqueId = UniqueId.of("abcdefgh"),
            variations = mapOf(
                VariationName.of("off") to Weight.of(1),
                VariationName.of("on") to Weight.of(2),
            ),
            defaultVariation = VariationName.of("off"),
            overrides = mapOf(
                SubjectId.of("testuser") to VariationName.of("on")
            )
        )
    }
}