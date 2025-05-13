package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.http.server.toResponse
import dev.andrewohara.toggles.toggles.getState
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestLens
import org.http4k.security.Security

fun engineApiV1(service: TogglesApp, security: Security, authLens: RequestLens<ApiKeyMeta>) = listOf(
    EngineRoutes.getToggleState(security) to { toggleName ->
        { request ->
            val principal = authLens(request)
            service.getState(principal.tenantId, principal.projectName, toggleName, principal.environment)
                .map { Response(Status.OK).with(ToggleStateDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    }
)

private fun ToggleState.toDto() = ToggleStateDto(
    uniqueId = uniqueId,
    variations = variations,
    defaultVariation = defaultVariation,
    overrides = overrides
)