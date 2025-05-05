package dev.andrewohara.toggles.http.server

import dev.andrewohara.toggles.ApiKey
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.hash
import dev.andrewohara.toggles.http.ProjectCreateDataDto
import dev.andrewohara.toggles.http.ProjectDto
import dev.andrewohara.toggles.http.ProjectUpdateDataDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.ToggleCreateDataDto
import dev.andrewohara.toggles.http.TogglesRoutes
import dev.andrewohara.toggles.http.ToggleUpdateDataDto
import dev.andrewohara.toggles.http.ToggleDto
import dev.andrewohara.toggles.http.ToggleStateDto
import dev.andrewohara.toggles.http.TogglesPageDto
import dev.andrewohara.toggles.http.TogglesRoutes.projectCursorLens
import dev.andrewohara.toggles.http.TogglesRoutes.toggleCursorLens
import dev.andrewohara.toggles.http.TogglesRoutes.toggleNameLens
import dev.andrewohara.toggles.projects.createProject
import dev.andrewohara.toggles.projects.deleteProject
import dev.andrewohara.toggles.projects.listProjects
import dev.andrewohara.toggles.projects.updateProject
import dev.andrewohara.toggles.toggles.createToggle
import dev.andrewohara.toggles.toggles.deleteToggle
import dev.andrewohara.toggles.toggles.getState
import dev.andrewohara.toggles.toggles.getToggle
import dev.andrewohara.toggles.toggles.listToggles
import dev.andrewohara.toggles.toggles.updateToggle
import dev.andrewohara.toggles.users.User
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import dev.forkhandles.values.parseOrNull
import org.http4k.contract.contract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Moshi
import org.http4k.lens.RequestKey
import org.http4k.routing.routes
import org.http4k.security.BearerAuthSecurity

val clientAuthLens = RequestKey.required<ApiKeyMeta>("client_auth")
val userAuthLens = RequestKey.required<User>("user_auth")

fun TogglesApp.toHttpServer(): HttpHandler {
    val clientSecurity = BearerAuthSecurity(clientAuthLens, lookup = { token ->
        ApiKey.parseOrNull(token)
            ?.let { hash(it) }
            ?.let(storage.apiKeys::get)
    })

    val api = contract {
        renderer = OpenApi3(
            ApiInfo("Toggles API", "1"),
            apiRenderer = OpenApi3ApiRenderer(Moshi),
            json = Moshi
        )
        descriptionPath = "openapi.json"

        routes += TogglesRoutes.listProjects to { request: Request ->
            val projects = listProjects(userAuthLens(request).tenantId,projectCursorLens(request))
            Response(Status.OK).with(ProjectsPageDto.lens of projects.toDto())
        }

        routes += TogglesRoutes.createProject to { request: Request ->
            val data = ProjectCreateDataDto.lens(request)
            createProject(userAuthLens(request).tenantId,data.toModel())
                .map { Response(Status.OK).with(ProjectDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }

        routes += TogglesRoutes.updateProject to { projectName ->
            { request ->
                val data = ProjectUpdateDataDto.lens(request)
                updateProject(userAuthLens(request).tenantId, projectName, data.toModel())
                    .map { Response(Status.OK).with(ProjectDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
            }
        }

        routes += TogglesRoutes.deleteProject to { projectName ->
            { request ->
                deleteProject(userAuthLens(request).tenantId, projectName)
                    .map { Response(Status.OK).with(ProjectDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
            }
        }

        routes += TogglesRoutes.listToggles to { projectName, _ ->
            { request ->
                listToggles(userAuthLens(request).tenantId, projectName, toggleCursorLens(request))
                    .map { Response(Status.OK).with(TogglesPageDto.lens of it.toDto()) }
                    .recover { it.toResponse()}
            }
        }

        routes += TogglesRoutes.getToggle to { projectName, _, toggleName ->
            { request ->
                getToggle(userAuthLens(request).tenantId, projectName, toggleName)
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
            }
        }

        routes += TogglesRoutes.createToggle to { projectName, _ ->
            { request ->
                val data = ToggleCreateDataDto.lens(request)
                createToggle(userAuthLens(request).tenantId, projectName, data.toModel())
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
            }
        }

        routes += TogglesRoutes.updateToggle to { projectName, _, toggleName ->
            { request ->
                val data = ToggleUpdateDataDto.lens(request)
                updateToggle(userAuthLens(request).tenantId, projectName, toggleName, data.toModel())
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse()}
            }
        }

        routes += TogglesRoutes.deleteToggle to { projectName, _, toggleName ->
            { request ->
                deleteToggle(userAuthLens(request).tenantId, projectName, toggleName)
                    .map { Response(Status.OK).with(ToggleDto.lens of it.toDto()) }
                    .recover { it.toResponse()}
            }
        }

        routes += "/v1/toggles" / toggleNameLens meta {
            operationId = "v1GetToggleState"
            summary = "Get Toggle State"
            security = clientSecurity

            returning(Status.OK, ToggleStateDto.lens to ToggleStateDto.sample)
        } bindContract Method.GET to { toggleName ->
            { request ->
                val principal = clientAuthLens(request)
                getState(userAuthLens(request).tenantId, principal.projectName, toggleName, principal.environment)
                    .map { Response(Status.OK).with(ToggleStateDto.lens of it.toDto()) }
                    .recover { it.toResponse() }
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