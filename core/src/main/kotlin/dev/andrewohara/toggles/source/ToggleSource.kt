package dev.andrewohara.toggles.source

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.Result4k

interface ToggleSource: AutoCloseable {
    operator fun invoke(toggleName: ToggleName): Result4k<ToggleState, String>

    companion object
}