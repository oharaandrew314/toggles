package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.toResponse
import dev.andrewohara.toggles.users.User
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestLens
import org.http4k.security.Security
import kotlin.collections.map

fun toggleApiV1(service: TogglesApp, security: Security, authLens: RequestLens<User>) = listOf(
    ToggleRoutes.listToggles(security) to { projectName, _ ->
        { request ->
            service.listToggles(authLens(request).tenantId, projectName, ToggleRoutes.toggleCursorLens(request))
                .map { Response(Status.OK).with(TogglesPageDto.lens of it.toDto()) }
                .recover { it.toResponse()}
        }
    },
    ToggleRoutes.getToggle(security) to { projectName, _, toggleName ->
        { request ->
            service.getToggle(authLens(request).tenantId, projectName, toggleName)
                .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    },
    ToggleRoutes.createToggle(security) to { projectName, _ ->
        { request ->
            val data = ToggleCreateDataDto.lens(request)
            service.createToggle(authLens(request), projectName, data.toModel())
                .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    },
    ToggleRoutes.updateToggle(security) to { projectName, _, toggleName ->
        { request ->
            val data = ToggleUpdateDataDto.lens(request)
            service.updateToggle(authLens(request).tenantId, projectName, toggleName, data.toModel())
                .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                .recover { it.toResponse()}
        }
    },
    ToggleRoutes.deleteToggle(security) to { projectName, _, toggleName ->
        { request ->
            service.deleteToggle(authLens(request), projectName, toggleName)
                .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                .recover { it.toResponse()}
        }
    }
)

fun Toggle.toDto() = ToggleDto(
    projectName = projectName,
    toggleName = toggleName,
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

private fun Page<Toggle, ToggleName>.toDto() = TogglesPageDto(
    items = items.map { it.toDto() },
    next = next
)

private fun ToggleUpdateDataDto.toModel() = ToggleUpdateData(
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { it.value.toModel() }
)

private fun ToggleCreateDataDto.toModel() = ToggleCreateData(
    toggleName = toggleName,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { it.value.toModel() }
)

private fun ToggleEnvironmentDto.toModel() = ToggleEnvironment(
    weights = variations,
    overrides = overrides
)