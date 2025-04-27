package dev.andrewohara.toggles.source

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr

fun ToggleSource.Companion.fake(
    toggles: Map<ToggleName, ToggleState>
) = ToggleSource { projectName, toggleName ->
    toggles[toggleName].asResultOr { "Toggle not found: $projectName/$toggleName" }
}

fun ToggleSource.Companion.fake(state: ToggleState) = ToggleSource { _, _ -> Success(state) }