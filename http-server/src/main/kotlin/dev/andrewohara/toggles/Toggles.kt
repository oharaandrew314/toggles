package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.ToggleStorage
import dev.andrewohara.toggles.storage.getProjectOrFail
import dev.andrewohara.toggles.storage.getToggleOrFail
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek
import java.time.Clock

class Toggles(
    val storage: ToggleStorage,
    val pageSize: Int = 100,
    val clock: Clock = Clock.systemUTC()
)

// Projects

fun Toggles.createProject(data: ProjectData) = begin
    .failIf({storage.getProject(data.projectName) != null}, { ProjectAlreadyExists(data.projectName)})
    .map { Project(data.projectName, clock.instant()) }
    .peek(storage::upsertProject)

fun Toggles.listProjects(cursor: ProjectName?) =
    storage.listProjects(pageSize)[cursor]

fun Toggles.deleteProject(projectName: ProjectName) = storage
    .getProjectOrFail(projectName)
    .failIf({storage.listToggles(projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .peek { storage.deleteProject(projectName) }

// Toggles

fun Toggles.listToggles(projectName: ProjectName, cursor: ToggleName?) = storage
    .getProjectOrFail(projectName)
    .map { storage.listToggles(projectName, pageSize)[cursor] }

fun Toggles.getToggle(projectName: ProjectName, toggleName: ToggleName) = storage
    .getProjectOrFail(projectName)
    .flatMap { storage.getToggleOrFail(projectName, toggleName) }

fun Toggles.createToggle(projectName: ProjectName, data: ToggleCreateData) = storage
    .getProjectOrFail(projectName)
    .failIf({ storage.getToggle(projectName, data.toggleName) != null}, { ToggleAlreadyExists(projectName, data.toggleName) })
    .map { data.toToggle(projectName, clock.instant()) }
    .peek(storage::upsertToggle)

fun Toggles.updateToggle(projectName: ProjectName, toggleName: ToggleName, data: ToggleState) = storage
    .getProjectOrFail(projectName)
    .flatMap { storage.getToggleOrFail(projectName, toggleName) }
    .map { it.with(data, clock.instant()) }
    .peek(storage::upsertToggle)

fun Toggles.deleteToggle(projectName: ProjectName, toggleName: ToggleName) = storage
    .getProjectOrFail(projectName)
    .flatMap { storage.getToggleOrFail(projectName, toggleName) }
    .peek { storage.deleteToggle(projectName, toggleName) }