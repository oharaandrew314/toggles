package dev.andrewohara.toggles.storage
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ToggleStorage {
    fun list(projectName: ProjectName, pageSize: Int): Paginator<Toggle, ToggleName>
    operator fun get(projectName: ProjectName, toggleName: ToggleName): Toggle?
    operator fun plusAssign(toggle: Toggle)
    fun remove(projectName: ProjectName, toggleName: ToggleName)
}

fun ToggleStorage.getOrFail(projectName: ProjectName, toggleName: ToggleName) =
    get(projectName, toggleName).asResultOr { ToggleNotFound(projectName, toggleName) }

