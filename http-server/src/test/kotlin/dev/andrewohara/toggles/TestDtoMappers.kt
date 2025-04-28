package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.ToggleCreateDataDto
import dev.andrewohara.toggles.http.ToggleUpdateDataDto

fun ToggleCreateData.toDto() = ToggleCreateDataDto(
    toggleName = toggleName,
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)

fun ToggleState.toDto() = ToggleUpdateDataDto(
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)