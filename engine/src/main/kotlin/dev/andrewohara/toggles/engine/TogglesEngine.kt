package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleSource
import dev.andrewohara.toggles.VariationName

class TogglesEngine(
    val projectName: ProjectName,
    private val toggleSource: ToggleSource,
    private val defaultVariation: VariationName = VariationName.of("off")
) {
    operator fun get(toggleName: ToggleName, defaultVariation: VariationName? = null) = Feature(
        projectName = projectName,
        toggleName = toggleName,
        toggleSource = toggleSource,
        defaultVariation = defaultVariation ?: this.defaultVariation
    )
}