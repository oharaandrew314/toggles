package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ToggleEnvironmentDto(
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>
)