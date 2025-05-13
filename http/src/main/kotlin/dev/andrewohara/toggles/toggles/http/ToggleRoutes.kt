package dev.andrewohara.toggles.toggles.http

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.projects.http.ProjectRoutes
import org.http4k.contract.Tag
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.value
import org.http4k.security.Security

object ToggleRoutes {
    val toggleNameLens = Path.value(ToggleName).of("toggle_name")
    val toggleCursorLens = Query.value(ToggleName).optional("cursor")
    private val tag = Tag("Toggles")

    fun listToggles(auth: Security?) = "/v1/projects" / ProjectRoutes.projectNameLens/ "toggles" meta {
        operationId = "v1ListToggles"
        summary = "List Toggles"
        tags += tag
        security = auth

        returning(Status.OK, TogglesPageDto.lens to TogglesPageDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
    } bindContract Method.GET

    fun createToggle(auth: Security?) = "/v1/projects" / ProjectRoutes.projectNameLens / "toggles" meta {
        operationId = "v1CreateToggle"
        summary = "Create Toggle"
        tags += tag
        security = auth

        receiving(ToggleCreateDataDto.lens to ToggleCreateDataDto.sample)
        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.toggleAlreadyExistsSample)
    } bindContract Method.POST

    fun getToggle(auth: Security?) = "/v1/projects" / ProjectRoutes.projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1GetToggle"
        summary = "Get Toggle"
        tags += tag
        security = auth

        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.GET

    fun updateToggle(auth: Security?) = "/v1/projects" / ProjectRoutes.projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1UpdateToggle"
        summary = "Update Toggle"
        tags += tag
        security = auth

        receiving(ToggleUpdateDataDto.lens to ToggleUpdateDataDto.sample)
        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.PUT

    fun deleteToggle(auth: Security?) = "/v1/projects" / ProjectRoutes.projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1DeleteToggle"
        summary = "Delete Toggle"
        tags += tag
        security = auth

        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.DELETE
}