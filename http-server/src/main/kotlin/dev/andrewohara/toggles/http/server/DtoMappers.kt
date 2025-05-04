package dev.andrewohara.toggles.http.server

import dev.andrewohara.toggles.ApiKeyNotFound
import dev.andrewohara.toggles.EnvironmentInUse
import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectAlreadyExists
import dev.andrewohara.toggles.ProjectCreateData
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotEmpty
import dev.andrewohara.toggles.ProjectNotFound
import dev.andrewohara.toggles.ProjectUpdateData
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleAlreadyExists
import dev.andrewohara.toggles.ToggleCreateData
import dev.andrewohara.toggles.ToggleEnvironment
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleNotFound
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.ToggleUpdateData
import dev.andrewohara.toggles.TogglesError
import dev.andrewohara.toggles.http.ProjectCreateDataDto
import dev.andrewohara.toggles.http.ProjectDto
import dev.andrewohara.toggles.http.ProjectUpdateDataDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.ToggleCreateDataDto
import dev.andrewohara.toggles.http.ToggleUpdateDataDto
import dev.andrewohara.toggles.http.ToggleDto
import dev.andrewohara.toggles.http.ToggleEnvironmentDto
import dev.andrewohara.toggles.http.ToggleStateDto
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
    environments = environments,
    createdOn = createdOn,
    updatedOn = updatedOn
)

fun ProjectCreateDataDto.toModel() = ProjectCreateData(
    projectName = projectName,
    environments = environments
)

fun ProjectUpdateDataDto.toModel() = ProjectUpdateData(
    environments = environments
)

fun TogglesError.toDto() = TogglesErrorDto(
    message = when(this) {
        is ToggleNotFound -> "Toggle not found: $projectName/$toggleName"
        is ProjectNotEmpty -> "Project not empty: $projectName"
        is ProjectNotFound -> "Project not found: $projectName"
        is ProjectAlreadyExists -> "Project already exists: $projectName"
        is ToggleAlreadyExists -> "Toggle already exists: $projectName/$toggleName"
        is EnvironmentInUse -> "Environment in use: $projectName/$environmentName"
        is ApiKeyNotFound -> "Api Key not found: $projectName/$environmentName"
    }
)

fun TogglesError.toResponse() = Response(when(this) {
    is ToggleNotFound, is ProjectNotFound -> Status.NOT_FOUND
    else -> Status.CONFLICT
}).with(TogglesErrorDto.lens of toDto())

fun Toggle.toDto() = ToggleDto(
    projectName = projectName,
    toggleName = toggleName,
    uniqueId = uniqueId,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { (_, env) ->
        ToggleEnvironmentDto(
            variations = env.weights,
            overrides = env.overrides
        )
    }
)

fun Page<Toggle, ToggleName>.toDto() = TogglesPageDto(
    items = items.map { it.toDto() },
    next = next
)

fun ToggleUpdateDataDto.toModel() = ToggleUpdateData(
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { it.value.toModel() }
)

fun ToggleCreateDataDto.toModel() = ToggleCreateData(
    toggleName = toggleName,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { it.value.toModel() }
)

fun ToggleState.toDto() = ToggleStateDto(
    uniqueId = uniqueId,
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)

private fun ToggleEnvironmentDto.toModel() = ToggleEnvironment(
    weights = variations,
    overrides = overrides
)