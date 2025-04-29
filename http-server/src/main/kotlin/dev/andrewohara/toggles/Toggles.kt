package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.ProjectStorage
import dev.andrewohara.toggles.storage.ToggleStorage
import dev.andrewohara.toggles.storage.getOrFail
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek
import java.time.Clock

class Toggles(
    val projects: ProjectStorage,
    val toggles: ToggleStorage,
    val pageSize: Int = 100,
    val clock: Clock = Clock.systemUTC()
)

// Projects

fun Toggles.createProject(data: ProjectData) = begin
    .failIf({projects[data.projectName] != null}, { ProjectAlreadyExists(data.projectName)})
    .map { Project(data.projectName, clock.instant()) }
    .peek(projects::plusAssign)

fun Toggles.listProjects(cursor: ProjectName?) =
    projects.list(pageSize)[cursor]

fun Toggles.deleteProject(projectName: ProjectName) = begin
    .failIf({toggles.list(projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .flatMap { projects.delete(projectName).asResultOr { ProjectNotFound(projectName) } }

// Toggles

fun Toggles.listToggles(projectName: ProjectName, cursor: ToggleName?) = projects
    .getOrFail(projectName)
    .map { toggles.list(projectName, pageSize)[cursor] }

fun Toggles.getToggle(projectName: ProjectName, toggleName: ToggleName) = projects
    .getOrFail(projectName)
    .flatMap { toggles[projectName, toggleName].asResultOr { ToggleNotFound(projectName, toggleName) } }

fun Toggles.createToggle(projectName: ProjectName, data: ToggleCreateData) = projects
    .getOrFail(projectName)
    .failIf({toggles[projectName, data.toggleName] != null}, { ToggleAlreadyExists(projectName, data.toggleName) })
    .map { data.toToggle(projectName, clock.instant()) }
    .peek(toggles::plusAssign)

fun Toggles.updateToggle(projectName: ProjectName, toggleName: ToggleName, data: ToggleState) = projects
    .getOrFail(projectName)
    .flatMap { toggles.getOrFail(projectName, toggleName) }
    .map { it.with(data, clock.instant()) }
    .peek(toggles::plusAssign)

fun Toggles.deleteToggle(projectName: ProjectName, toggleName: ToggleName) = projects
    .getOrFail(projectName)
    .flatMap { toggles.delete(projectName, toggleName).asResultOr { ToggleNotFound(projectName, toggleName) } }