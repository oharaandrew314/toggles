package dev.andrewohara.toggles.http.server

import dev.andrewohara.toggles.Toggles
import dev.andrewohara.toggles.createProject
import dev.andrewohara.toggles.createToggle
import dev.andrewohara.toggles.deleteProject
import dev.andrewohara.toggles.deleteToggle
import dev.andrewohara.toggles.getToggle
import dev.andrewohara.toggles.http.ProjectDataDto
import dev.andrewohara.toggles.http.ProjectDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.ToggleCreateDataDto
import dev.andrewohara.toggles.http.TogglesRoutes
import dev.andrewohara.toggles.http.ToggleUpdateDataDto
import dev.andrewohara.toggles.http.ToggleDto
import dev.andrewohara.toggles.http.TogglesPageDto
import dev.andrewohara.toggles.http.TogglesRoutes.projectCursorLens
import dev.andrewohara.toggles.http.TogglesRoutes.toggleCursorLens
import dev.andrewohara.toggles.listProjects
import dev.andrewohara.toggles.listToggles
import dev.andrewohara.toggles.updateToggle
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Moshi
import org.http4k.routing.routes

fun Toggles.toHttpServer(): HttpHandler {
    val api = contract {
        renderer = OpenApi3(
            ApiInfo("Toggles API", "1"),
            apiRenderer = OpenApi3ApiRenderer(Moshi),
            json = Moshi
        )
        descriptionPath = "openapi.json"

        routes += TogglesRoutes.listProjects to { request: Request ->
            val projects = listProjects(cursor = projectCursorLens(request))
            Response(Status.OK).with(ProjectsPageDto.lens of projects.toDto())
        }

        routes += TogglesRoutes.createProject to { request: Request ->
            val data = ProjectDataDto.lens(request)
            createProject(data.toModel())
                .map { Response(Status.OK).with(ProjectDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }

        routes += TogglesRoutes.deleteProject to { projectName ->
            {
                deleteProject(projectName)
                    .map { Response(Status.OK).with(ProjectDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
            }
        }

        routes += TogglesRoutes.listToggles to { projectName, _ ->
            { request ->
                listToggles(projectName, toggleCursorLens(request))
                    .map { Response(Status.OK).with(TogglesPageDto.lens of it.toDto()) }
                    .recover { it.toResponse()}
            }
        }

        routes += TogglesRoutes.getToggle to { projectName, _, toggleName ->
            {
                getToggle(projectName, toggleName)
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
            }
        }

        routes += TogglesRoutes.createToggle to { projectName, _ ->
            { request ->
                val data = ToggleCreateDataDto.lens(request)
                createToggle(projectName, data.toModel())
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
            }
        }

        routes += TogglesRoutes.updateToggle to { projectName, _, toggleName ->
            { request ->
                val data = ToggleUpdateDataDto.lens(request)
                updateToggle(projectName, toggleName, data.toModel())
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse()}
            }
        }

        routes += TogglesRoutes.deleteToggle to { projectName, _, toggleName ->
            {
                deleteToggle(projectName, toggleName)
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse()}
            }
        }
    }

    val ui = swaggerUiLite {
        pageTitle = "Toggles API"
        url = "openapi.json"

        displayOperationId = true
        displayRequestDuration = true
        requestSnippetsEnabled = true
        tryItOutEnabled = true
        deepLinking = true
    }

    return routes(api, ui)
}