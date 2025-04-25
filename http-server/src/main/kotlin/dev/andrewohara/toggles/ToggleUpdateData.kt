package dev.andrewohara.toggles

import java.time.Instant

data class ToggleUpdateData(
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>,
    val defaultVariation: VariationName
)

fun Toggle.with(data: ToggleUpdateData, time: Instant) = copy(
    updatedOn = time,
    variations = data.variations,
    overrides = data.overrides,
    defaultVariation = data.defaultVariation
)