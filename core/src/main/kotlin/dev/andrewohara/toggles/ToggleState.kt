package dev.andrewohara.toggles

data class ToggleState(
    val variations: Map<VariationName, Weight>,
    val defaultVariation: VariationName,
    val overrides: Map<SubjectId, VariationName>
) {
    companion object {
        val default get() = ToggleState(
            variations = mapOf(VariationName.default to Weight.of(1)),
            defaultVariation = VariationName.default,
            overrides = emptyMap()
        )
    }
}