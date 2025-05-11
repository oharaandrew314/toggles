package dev.andrewohara.toggles

sealed interface TogglesError

data class TenantNotFound(val tenantId: TenantId): TogglesError

data class ProjectAlreadyExists(val projectName: ProjectName): TogglesError
data class ProjectNotFound(val projectName: ProjectName): TogglesError
data class ProjectNotEmpty(val projectName: ProjectName): TogglesError
data class EnvironmentInUse(val projectName: ProjectName, val environmentName: EnvironmentName): TogglesError

data class ToggleAlreadyExists(val projectName: ProjectName, val toggleName: ToggleName): TogglesError
data class ToggleNotFound(val projectName: ProjectName, val toggleName: ToggleName): TogglesError

data class ApiKeyNotFound(val projectName: ProjectName, val environmentName: EnvironmentName): TogglesError

data class UserNotFound(val userId: UniqueId): TogglesError
data class UserAlreadyExists(val tenantId: TenantId, val emailAddress: EmailAddress): TogglesError
