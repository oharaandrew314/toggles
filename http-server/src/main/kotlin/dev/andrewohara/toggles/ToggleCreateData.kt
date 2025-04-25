package dev.andrewohara.toggles

import java.time.Instant

data class ToggleCreateData(
    val toggleName: ToggleName,
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>,
    val defaultVariation: VariationName
)

fun ToggleCreateData.toToggle(projectName: ProjectName, time: Instant) = Toggle(
    projectName = projectName,
    toggleName = toggleName,
    createdOn = time,
    updatedOn = time,
    variations = variations,
    overrides = overrides,
    defaultVariation = defaultVariation
)