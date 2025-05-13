package dev.andrewohara.toggles.http.server

import dev.andrewohara.toggles.ApiKeyNotFound
import dev.andrewohara.toggles.EnvironmentInUse
import dev.andrewohara.toggles.ProjectAlreadyExists
import dev.andrewohara.toggles.ProjectNotEmpty
import dev.andrewohara.toggles.ProjectNotFound
import dev.andrewohara.toggles.RequiresAdmin
import dev.andrewohara.toggles.RequiresAdminOrDeveloper
import dev.andrewohara.toggles.TenantNotFound
import dev.andrewohara.toggles.ToggleAlreadyExists
import dev.andrewohara.toggles.ToggleNotFound
import dev.andrewohara.toggles.TogglesError
import dev.andrewohara.toggles.UserAlreadyExists
import dev.andrewohara.toggles.UserIsPrincipal
import dev.andrewohara.toggles.UserNotFound
import dev.andrewohara.toggles.UserNotFoundByEmail
import dev.andrewohara.toggles.http.TogglesErrorDto
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