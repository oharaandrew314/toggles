package dev.andrewohara.toggles

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun TogglesError.toDto() = TogglesErrorDto(
    message = when(this) {
        is ToggleNotFound -> "Toggle not found: $projectName/$toggleName"
        is ProjectNotEmpty -> "Project not empty: $projectName"
        is ProjectNotFound -> "Project not found: $projectName"
        is ProjectAlreadyExists -> "Project already exists: $projectName"
        is ToggleAlreadyExists -> "Toggle already exists: $projectName/$toggleName"
        is EnvironmentInUse -> "Environment in use: $projectName/$environmentName"
        is ApiKeyNotFound -> "Api Key not found: $projectName/$environmentName"
        is TenantNotFound -> "Tenant not found: $tenantId"
        is UserNotFound -> "User not found: $userId"
        is UserAlreadyExists -> "User already exists: $emailAddress"
        is UserNotFoundByEmail -> "User not found: $emailAddress"
        RequiresAdmin -> "You must be an admin to perform this action"
        RequiresAdminOrDeveloper -> "You must be an admin or developer to perform this action"
        UserIsPrincipal -> "You cannot perform this action on yourself"
    }
)

fun TogglesError.toResponse() = Response(when(this) {
    is ToggleNotFound, is ProjectNotFound -> Status.NOT_FOUND
    else -> Status.CONFLICT
}).with(TogglesErrorDto.lens of toDto())