package dev.andrewohara.toggles

import dev.andrewohara.toggles.repo.ProjectRepo
import dev.andrewohara.toggles.repo.TogglesRepo
import dev.andrewohara.toggles.repo.getOrFail
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek
import java.time.Clock

class Toggles(
    val projects: ProjectRepo,
    val toggles: TogglesRepo,
    val pageSize: Int,
    val clock: Clock
)

// Projects

fun Toggles.createProject(projectName: ProjectName) = begin
    .failIf({projects[projectName] != null}, { ProjectAlreadyExists(projectName)})
    .map { Project(projectName, clock.instant()) }
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

fun Toggles.createToggle(projectName: ProjectName, toggleName: ToggleName, data: ToggleData) = projects
    .getOrFail(projectName)
    .failIf({toggles[projectName, toggleName] != null}, { ToggleAlreadyExists(projectName, toggleName) })
    .map { data.toToggle(projectName, toggleName,clock.instant()) }
    .peek(toggles::plusAssign)

fun Toggles.updateToggle(projectName: ProjectName, toggleName: ToggleName, data: ToggleData) = projects
    .getOrFail(projectName)
    .flatMap { toggles.getOrFail(projectName, toggleName) }
    .map { it.with(data, clock.instant()) }
    .peek(toggles::plusAssign)

fun Toggles.deleteToggle(projectName: ProjectName, toggleName: ToggleName) = projects
    .getOrFail(projectName)
    .peek { toggles.delete(projectName, toggleName).asResultOr { ToggleNotFound(projectName, toggleName) } }