package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.toggles.source.ToggleSource
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import java.time.Instant

fun Toggle.with(data: ToggleState, time: Instant) = copy(
    updatedOn = time,
    variations = data.variations,
    overrides = data.overrides,
    defaultVariation = data.defaultVariation
)

private fun Toggle.toState() = ToggleState(
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)

fun Toggles.toToggleSource() = ToggleSource { projectName, toggleName ->
    getToggle(projectName, toggleName)
        .mapFailure { it.toDto().message }
        .map { it.toState() }
}