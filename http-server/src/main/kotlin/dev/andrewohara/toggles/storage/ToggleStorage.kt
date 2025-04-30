package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotFound
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ToggleStorage {
    companion object

    // Projects
    fun listProjects(pageSize: Int): Paginator<Project, ProjectName>
    fun getProject(projectName: ProjectName): Project?
    fun upsertProject(project: Project)
    fun deleteProject(projectName: ProjectName)

    // Toggles
    fun listToggles(projectName: ProjectName, pageSize: Int): Paginator<Toggle, ToggleName>
    fun getToggle(projectName: ProjectName, toggleName: ToggleName): Toggle?
    fun upsertToggle(toggle: Toggle)
    fun deleteToggle(projectName: ProjectName, toggleName: ToggleName)
}

fun ToggleStorage.getToggleOrFail(projectName: ProjectName, toggleName: ToggleName) =
    getToggle(projectName, toggleName).asResultOr { ToggleNotFound(projectName, toggleName) }

fun ToggleStorage.getProjectOrFail(projectName: ProjectName) =
    getProject(projectName).asResultOr { ProjectNotFound(projectName) }