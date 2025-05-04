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

object TogglesRoutes {
    val projectNameLens = Path.value(ProjectName).of("project_name")
    val toggleNameLens = Path.value(ToggleName).of("toggle_name")

    val projectCursorLens = Query.value(ProjectName).optional("cursor")
    val toggleCursorLens = Query.value(ToggleName).optional("cursor")

    val listProjects = "/v1/projects" meta {
        operationId = "v1ListProjects"
        summary = "List Projects"
        tags += ProjectDto.tag

        returning(Status.OK, ProjectsPageDto.lens to ProjectsPageDto.sample)
    } bindContract Method.GET

    val createProject = "/v1/projects" meta {
        operationId = "v1CreateProject"
        summary = "Create Project"
        tags += ProjectDto.tag

        receiving(ProjectCreateDataDto.lens to ProjectCreateDataDto.sample)
        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.projectAlreadyExistsSample)
    } bindContract Method.POST

    val updateProject = "/v1/projects" / projectNameLens meta {
        operationId = "v1UpdateProject"
        summary = "Update Project"
        tags += ProjectDto.tag

        receiving(ProjectUpdateDataDto.lens to ProjectUpdateDataDto.sample)
        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.projectAlreadyExistsSample)
    } bindContract Method.PUT

    val deleteProject = "/v1/projects" / projectNameLens meta {
        operationId = "v1DeleteProject"
        summary = "Delete Project"
        tags += ProjectDto.tag

        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
    } bindContract Method.DELETE

    val listToggles = "/v1/projects" / projectNameLens / "toggles" meta {
        operationId = "v1ListToggles"
        summary = "List Toggles"
        tags += ToggleDto.tag

        returning(Status.OK, ToggleDto.manyLens to arrayOf(ToggleDto.sample))
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
    } bindContract Method.GET

    val createToggle = "/v1/projects" / projectNameLens / "toggles" meta {
        operationId = "v1CreateToggle"
        summary = "Create Toggle"
        tags += ToggleDto.tag

        receiving(ToggleCreateDataDto.lens to ToggleCreateDataDto.sample)
        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.toggleAlreadyExistsSample)
    } bindContract Method.POST

    val getToggle = "/v1/projects" / projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1GetToggle"
        summary = "Get Toggle"
        tags += ToggleDto.tag

        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.GET

    val updateToggle = "/v1/projects" / projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1UpdateToggle"
        summary = "Update Toggle"
        tags += ToggleDto.tag

        receiving(ToggleUpdateDataDto.lens to ToggleUpdateDataDto.sample)
        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.PUT

    val deleteToggle = "/v1/projects" / projectNameLens / "toggles" / toggleNameLens meta {
        operationId = "v1DeleteToggle"
        summary = "Delete Toggle"
        tags += ToggleDto.tag

        returning(Status.OK, ToggleDto.lens to ToggleDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.toggleDoesNotExistSample)
    } bindContract Method.DELETE
}