package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.source.ToggleSource

class TogglesEngine(
    private val toggleSource: ToggleSource,
    private val defaultVariation: VariationName = VariationName.default
) {
    operator fun get(toggleName: ToggleName, defaultVariation: VariationName? = null) = FeatureFlag(
        toggleName = toggleName,
        toggleSource = toggleSource,
        defaultVariation = defaultVariation ?: this.defaultVariation
    )
}