package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.value
import org.http4k.security.Security

object TogglesRoutes {
    val projectNameLens = Path.value(ProjectName).of("project_name")
    val toggleNameLens = Path.value(ToggleName).of("toggle_name")

    val projectCursorLens = Query.value(ProjectName).optional("cursor")
    val toggleCursorLens = Query.value(ToggleName).optional("cursor")

    fun getToggleState(auth: Security) = "/v1/toggles" / toggleNameLens meta {
        operationId = "v1GetToggleState"
        summary = "Get Toggle State"
        security = auth

        returning(Status.OK, ToggleStateDto.lens to ToggleStateDto.sample)
    } bindContract Method.GET

    fun listProjects(auth: Security?) = "/v1/projects" meta {
        operationId = "v1ListProjects"
        summary = "List Projects"
        tags += ProjectDto.tag
        security = auth

        returning(Status.OK, ProjectsPageDto.lens to ProjectsPageDto.sample)
    } bindContract Method.GET

    fun createProject(auth: Security?) = "/v1/projects" meta {
        operationId = "v1CreateProject"
        summary = "Create Project"
        tags += ProjectDto.tag
        security = auth

        receiving(ProjectCreateDataDto.lens to ProjectCreateDataDto.sample)
        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.projectAlreadyExistsSample)
    } bindContract Method.POST

    fun updateProject(auth: Security?) = "/v1/projects" / projectNameLens meta {
        operationId = "v1UpdateProject"
        summary = "Update Project"
        tags += ProjectDto.tag
        security = auth

        receiving(ProjectUpdateDataDto.lens to ProjectUpdateDataDto.sample)
        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.projectAlreadyExistsSample)
    } bindContract Method.PUT

    fun deleteProject(auth: Security?) = "/v1/projects" / projectNameLens meta {
        operationId = "v1DeleteProject"
        summary = "Delete Project"
        tags += ProjectDto.tag
        security = auth

        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
    } bindContract Method.DELETE

    fun listToggles(auth: Security?) = "/v1/projects" / projectNameLens / "toggles" meta {
        operationId = "v1ListToggles"
        summary = "List Toggles"
        tags += ToggleDto.tag
        security = auth

        returning(Status.OK, ToggleDto.manyLens to arrayOf(ToggleDto.sample))
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
    } bindContract Method.GET

    fun createToggle(auth: Security?) = "/v1/projects" / projectNameLens / "toggles" meta {
        operationId = "v1CreateToggle"
        summary = "Create Toggle"
        tags += ToggleDto.tag
        security = auth

        receiving(ToggleCreateDataDto.lens to ToggleCreateDataDto.sample)
        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.toggleAlreadyExistsSample)
    } bindContract Method.POST

    fun getToggle(auth: Security?) = "/v1/projects" / projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1GetToggle"
        summary = "Get Toggle"
        tags += ToggleDto.tag
        security = auth

        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.GET

    fun updateToggle(auth: Security?) = "/v1/projects" / projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1UpdateToggle"
        summary = "Update Toggle"
        tags += ToggleDto.tag
        security = auth

        receiving(ToggleUpdateDataDto.lens to ToggleUpdateDataDto.sample)
        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.PUT

    fun deleteToggle(auth: Security?) = "/v1/projects" / projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1DeleteToggle"
        summary = "Delete Toggle"
        tags += ToggleDto.tag
        security = auth

        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.DELETE
}