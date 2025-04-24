package dev.andrewohara.toggles

sealed interface TogglesError

data class ProjectAlreadyExists(val projectName: ProjectName): TogglesError
data class ProjectNotFound(val projectName: ProjectName): TogglesError
data class ToggleAlreadyExists(val projectName: ProjectName, val toggleName: ToggleName): TogglesError
data class ToggleNotFound(val projectName: ProjectName, val toggleName: ToggleName): TogglesError
data class ProjectNotEmpty(val projectName: ProjectName): TogglesError