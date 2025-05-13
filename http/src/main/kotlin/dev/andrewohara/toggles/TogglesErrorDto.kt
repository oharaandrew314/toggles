package dev.andrewohara.toggles

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class TogglesErrorDto(
    val message: String
) {
    companion object {
        val lens = togglesJson.autoBody<TogglesErrorDto>().toLens()

        val userNotFoundSample = TogglesErrorDto(
            message = "user not found: abc123"
        )
        val userAlreadyExistsSample = TogglesErrorDto(
            message = "user already exists: user@domain.com"
        )

        val projectAlreadyExistsSample = TogglesErrorDto(
            message = "project already exists: my_project"
        )
        val projectDoesNotExistSample = TogglesErrorDto(
            message = "project does not exist: my_project"
        )
        val environmentInUseSample = TogglesErrorDto(
            message = "environment in use: my_project/production"
        )

        val toggleAlreadyExistsSample = TogglesErrorDto(
            message = "toggle already exists: my_project/my_toggle"
        )
        val toggleDoesNotExistSample = TogglesErrorDto(
            message = "toggle does not exist: my_project/my_toggle"
        )
    }
}