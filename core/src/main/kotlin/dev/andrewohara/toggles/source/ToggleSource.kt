package dev.andrewohara.toggles.source

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.Result4k

fun interface ToggleSource {
    operator fun invoke(projectName: ProjectName, toggleName: ToggleName): Result4k<ToggleState, String>

    companion object
}