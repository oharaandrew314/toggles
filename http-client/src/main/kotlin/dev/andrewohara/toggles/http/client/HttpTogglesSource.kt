package dev.andrewohara.toggles.http.client

import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure

fun TogglesHttpClient.toToggleSource() = ToggleSource { projectName, toggleName ->
    getToggle(projectName, toggleName)
        .mapFailure { it.message }
        .map { ToggleState(variations = it.variations, defaultVariation = it.defaultVariation, overrides = it.overrides) }
}