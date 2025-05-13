package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.togglesJson
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ToggleUpdateDataDto(
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironmentDto>
) {
    companion object {
        val lens = togglesJson.autoBody<ToggleUpdateDataDto>().toLens()

        val sample = ToggleUpdateDataDto(
            variations = listOf(
                VariationName.Companion.of("off"),
                VariationName.Companion.of("on")
            ),
            defaultVariation = VariationName.Companion.of("off"),
            environments = mapOf(
                EnvironmentName.Companion.of("dev") to ToggleEnvironmentDto(
                    variations = mapOf(
                        VariationName.Companion.of("off") to Weight.Companion.of(0),
                        VariationName.Companion.of("on") to Weight.Companion.of(1)
                    ),
                    overrides = emptyMap()
                ),
                EnvironmentName.Companion.of("prod") to ToggleEnvironmentDto(
                    variations = mapOf(
                        VariationName.Companion.of("off") to Weight.Companion.of(2),
                        VariationName.Companion.of("on") to Weight.Companion.of(1)
                    ),
                    overrides = mapOf(
                        SubjectId.Companion.of("testuser") to VariationName.Companion.of("on")
                    )
                )
            )
        )
    }
}