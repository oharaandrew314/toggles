package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.ToggleCreateDataDto
import dev.andrewohara.toggles.http.ToggleEnvironmentDto
import dev.andrewohara.toggles.http.ToggleUpdateDataDto

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