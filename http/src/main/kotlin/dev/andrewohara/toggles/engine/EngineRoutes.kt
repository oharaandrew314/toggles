package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.toggles.ToggleRoutes.toggleNameLens
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.security.Security

object EngineRoutes {
    fun getToggleState(auth: Security) = "/v1/toggles" / toggleNameLens meta {
        operationId = "v1GetToggleState"
        summary = "Get Toggle State"
        security = auth

        returning(Status.OK, ToggleStateDto.lens to ToggleStateDto.sample)
    } bindContract Method.GET
}