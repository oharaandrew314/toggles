package dev.andrewohara.toggles.source

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr

fun ToggleSource.Companion.fake(
    toggles: Map<ToggleName, ToggleState>
) = object: ToggleSource {
    override fun invoke(toggleName: ToggleName): Result4k<ToggleState, String> {
        return toggles[toggleName].asResultOr { "Toggle not found: $toggleName" }
    }

    override fun close() {}
}


fun ToggleSource.Companion.fake(state: ToggleState) = object: ToggleSource {
    override fun invoke(toggleName: ToggleName) = Success(state)
    override fun close() {}
}