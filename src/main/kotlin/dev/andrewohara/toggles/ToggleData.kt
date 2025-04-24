package dev.andrewohara.toggles

import java.time.Instant

data class ToggleData(
    val variations: Map<VariationName, Weight>,
    val overrides: Map<String, VariationName>,
    val defaultVariation: VariationName
)

fun ToggleData.toToggle(projectName: ProjectName, toggleName: ToggleName, time: Instant) = Toggle(
    projectName = projectName,
    toggleName = toggleName,
    createdOn = time,
    updatedOn = time,
    variations = variations,
    overrides = overrides,
    defaultVariation = defaultVariation
)

fun Toggle.with(data: ToggleData, time: Instant) = copy(
    updatedOn = time,
    variations = data.variations,
    overrides = data.overrides,
    defaultVariation = data.defaultVariation
)