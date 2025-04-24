package dev.andrewohara.toggles.repo

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface TogglesRepo {
    companion object

    fun list(projectName: ProjectName, pageSize: Int): Paginator<Toggle, ToggleName>
    operator fun get(projectName: ProjectName, toggleName: ToggleName): Toggle?
    operator fun plusAssign(toggle: Toggle)
    operator fun minusAssign(toggle: Toggle)
    fun delete(projectName: ProjectName, toggleName: ToggleName): Toggle?
}

fun TogglesRepo.getOrFail(projectName: ProjectName, toggleName: ToggleName) =
    get(projectName, toggleName).asResultOr { ToggleNotFound(projectName, toggleName) }