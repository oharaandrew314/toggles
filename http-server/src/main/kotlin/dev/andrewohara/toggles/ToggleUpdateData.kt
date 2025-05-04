package dev.andrewohara.toggles

import java.time.Instant

data class ToggleUpdateData(
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironment>
)

fun Toggle.update(data: ToggleUpdateData, time: Instant) = copy(
    updatedOn = time,
    variations = data.variations,
    defaultVariation = data.defaultVariation,
    environments = data.environments
)