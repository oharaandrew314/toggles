package dev.andrewohara.toggles

import dev.andrewohara.toggles.toggles.ToggleCreateDataDto
import dev.andrewohara.toggles.toggles.ToggleEnvironmentDto
import dev.andrewohara.toggles.toggles.ToggleUpdateDataDto
import dev.andrewohara.toggles.toggles.ToggleCreateData
import dev.andrewohara.toggles.toggles.ToggleUpdateData

fun ToggleCreateData.toDto() = ToggleCreateDataDto(
    toggleName = toggleName,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { (_, env) -> ToggleEnvironmentDto(env.weights, env.overrides) }
)

fun ToggleUpdateData.toDto() = ToggleUpdateDataDto(
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { (_, env) -> ToggleEnvironmentDto(env.weights, env.overrides) }
)