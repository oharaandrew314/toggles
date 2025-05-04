package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
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
                VariationName.of("off"),
                VariationName.of("on")
            ),
            defaultVariation = VariationName.of("off"),
            environments = mapOf(
                EnvironmentName.of("dev") to ToggleEnvironmentDto(
                    variations = mapOf(
                        VariationName.of("off") to Weight.of(0),
                        VariationName.of("on") to Weight.of(1)
                    ),
                    overrides = emptyMap()
                ),
                EnvironmentName.of("prod") to ToggleEnvironmentDto(
                    variations = mapOf(
                        VariationName.of("off") to Weight.of(2),
                        VariationName.of("on") to Weight.of(1)
                    ),
                    overrides = mapOf(
                        SubjectId.of("testuser") to VariationName.of("on")
                    )
                )
            )
        )
    }
}