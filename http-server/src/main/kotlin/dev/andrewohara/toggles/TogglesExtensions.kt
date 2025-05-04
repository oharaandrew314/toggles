package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.toggles.source.ToggleSource
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure


fun Toggle.toState(environment: EnvironmentName) = ToggleState(
    uniqueId = uniqueId,
    variations = environments[environment]?.weights ?: emptyMap(),
    overrides = environments[environment]?.overrides ?: emptyMap(),
    defaultVariation = defaultVariation,
)

fun Toggles.toToggleSource(projectName: ProjectName, environment: EnvironmentName) = object: ToggleSource {

    override fun invoke(toggleName: ToggleName) = getToggle(projectName, toggleName)
        .mapFailure { it.toDto().message }
        .map { it.toState(environment) }

    override fun close() {}
}