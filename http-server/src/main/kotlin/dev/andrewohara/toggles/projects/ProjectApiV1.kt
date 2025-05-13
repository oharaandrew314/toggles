package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.toResponse
import dev.andrewohara.toggles.users.User
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestLens
import org.http4k.security.Security

fun projectApiV1(service: TogglesApp, security: Security, authLens: RequestLens<User>) = listOf(
    ProjectRoutes.listProjects(security) to { request: Request ->
        val projects = service.listProjects(authLens(request).tenantId, ProjectRoutes.projectCursorLens(request))
        Response.Companion(Status.Companion.OK).with(ProjectsPageDto.Companion.lens of projects.toDto())
    },

    ProjectRoutes.createProject(security) to { request: Request ->
        val data = ProjectCreateDataDto.lens(request)
        service.createProject(authLens(request),data.toModel())
            .map { Response.Companion(Status.Companion.OK).with(ProjectDto.Companion.lens of it.toDto()) }
            .recover { it.toResponse() }
    },

    ProjectRoutes.updateProject(security) to { projectName ->
        { request ->
            val data = ProjectUpdateDataDto.lens(request)
            service.updateProject(authLens(request), projectName, data.toModel())
                .map { Response.Companion(Status.Companion.OK).with(ProjectDto.Companion.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    },

    ProjectRoutes.deleteProject(security) to { projectName ->
        { request ->
            service.deleteProject(authLens(request), projectName)
                .map { Response.Companion(Status.Companion.OK).with(ProjectDto.Companion.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    }
)

private fun Page<Project, ProjectName>.toDto() = ProjectsPageDto(
    items = items.map { it.toDto() },
    next = next
)

fun Project.toDto() = ProjectDto(
    projectName = projectName,
    environments = environments,
    createdOn = createdOn,
    updatedOn = updatedOn
)

private fun ProjectCreateDataDto.toModel() = ProjectCreateData(
    projectName = projectName,
    environments = environments
)

private fun ProjectUpdateDataDto.toModel() = ProjectUpdateData(
    environments = environments
)