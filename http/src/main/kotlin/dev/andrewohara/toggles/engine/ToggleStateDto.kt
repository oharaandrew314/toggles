package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.togglesJson
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
            uniqueId = UniqueId.Companion.of("abcdefgh"),
            variations = mapOf(
                VariationName.Companion.of("off") to Weight.Companion.of(1),
                VariationName.Companion.of("on") to Weight.Companion.of(2),
            ),
            defaultVariation = VariationName.Companion.of("off"),
            overrides = mapOf(
                SubjectId.Companion.of("testuser") to VariationName.Companion.of("on")
            )
        )
    }
}