package dev.andrewohara.toggles

import dev.forkhandles.result4k.Result4k

fun interface ToggleSource {
    operator fun invoke(projectName: ProjectName, toggleName: ToggleName): Result4k<ToggleState, String>
}