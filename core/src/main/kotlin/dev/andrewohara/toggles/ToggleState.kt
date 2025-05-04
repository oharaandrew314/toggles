package dev.andrewohara.toggles

data class ToggleState(
    val uniqueId: UniqueId,
    val variations: Map<VariationName, Weight>,
    val defaultVariation: VariationName,
    val overrides: Map<SubjectId, VariationName>
)