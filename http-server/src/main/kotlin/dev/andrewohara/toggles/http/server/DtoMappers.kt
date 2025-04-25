package dev.andrewohara.toggles.http.server

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectAlreadyExists
import dev.andrewohara.toggles.ProjectData
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotEmpty
import dev.andrewohara.toggles.ProjectNotFound
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleAlreadyExists
import dev.andrewohara.toggles.ToggleCreateData
import dev.andrewohara.toggles.ToggleUpdateData
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleNotFound
import dev.andrewohara.toggles.TogglesError
import dev.andrewohara.toggles.http.ProjectDataDto
import dev.andrewohara.toggles.http.ProjectDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.ToggleCreateDataDto
import dev.andrewohara.toggles.http.ToggleUpdateDataDto
import dev.andrewohara.toggles.http.ToggleDto
import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.http.TogglesPageDto
import dev.andrewohara.utils.pagination.Page
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun Page<Project, ProjectName>.toDto() = ProjectsPageDto(
    items = items.map { it.toDto() },
    next = next
)

fun Project.toDto() = ProjectDto(
    projectName = projectName,
    createdOn = createdOn
)

fun ProjectDataDto.toModel() = ProjectData(
    projectName = projectName
)

fun TogglesError.toDto() = TogglesErrorDto(
    message = when(this) {
        is ToggleNotFound -> "Toggle not found: $projectName/$toggleName"
        is ProjectNotEmpty -> "Project not empty: $projectName"
        is ProjectNotFound -> "Project not found: $projectName"
        is ProjectAlreadyExists -> "Project already exists: $projectName"
        is ToggleAlreadyExists -> "Toggle already exists: $projectName/$toggleName"
    }
)

fun TogglesError.toResponse() = Response(when(this) {
    is ToggleNotFound, is ProjectNotFound -> Status.NOT_FOUND
    else -> Status.CONFLICT
}).with(TogglesErrorDto.lens of toDto())

fun Toggle.toDto() = ToggleDto(
    projectName = projectName,
    toggleName = toggleName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)

fun Page<Toggle, ToggleName>.toDto() = TogglesPageDto(
    items = items.map { it.toDto() },
    next = next
)

fun ToggleUpdateDataDto.toModel() = ToggleUpdateData(
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)

fun ToggleCreateDataDto.toModel() = ToggleCreateData(
    toggleName = toggleName,
    variations = variations,
    overrides = overrides,
    defaultVariation = defaultVariation
)