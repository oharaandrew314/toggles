package dev.andrewohara.toggles.projects.http

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.http.TogglesErrorDto
import org.http4k.contract.Tag
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.value
import org.http4k.security.Security

object ProjectRoutes {
    val projectNameLens = Path.value(ProjectName).of("project_name")
    val projectCursorLens = Query.value(ProjectName).optional("cursor")
    private val tag = Tag("Projects")

    fun listProjects(auth: Security?) = "/v1/projects" meta  {
        operationId = "v1ListProjects"
        summary = "List Projects"
        tags += tag
        security = auth

        returning(Status.OK, ProjectsPageDto.lens to ProjectsPageDto.sample)
    } bindContract Method.GET

    fun createProject(auth: Security?) = "/v1/projects" meta {
        operationId = "v1CreateProject"
        summary = "Create Project"
        tags += tag
        security = auth

        receiving(ProjectCreateDataDto.lens to ProjectCreateDataDto.sample)
        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.projectAlreadyExistsSample)
    } bindContract Method.POST

    fun updateProject(auth: Security?) = "/v1/projects" / projectNameLens meta {
        operationId = "v1UpdateProject"
        summary = "Update Project"
        tags += tag
        security = auth

        receiving(ProjectUpdateDataDto.lens to ProjectUpdateDataDto.sample)
        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.projectAlreadyExistsSample)
    } bindContract Method.PUT

    fun deleteProject(auth: Security?) = "/v1/projects" / projectNameLens meta {
        operationId = "v1DeleteProject"
        summary = "Delete Project"
        tags += tag
        security = auth

        returning(Status.OK, ProjectDto.lens to ProjectDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.projectDoesNotExistSample)
    } bindContract Method.DELETE
}