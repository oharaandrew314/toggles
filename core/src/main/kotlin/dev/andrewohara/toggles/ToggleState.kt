package dev.andrewohara.toggles

data class ToggleState(
    val variations: Map<VariationName, Weight>,
    val defaultVariation: VariationName,
    val overrides: Map<SubjectId, VariationName>
)