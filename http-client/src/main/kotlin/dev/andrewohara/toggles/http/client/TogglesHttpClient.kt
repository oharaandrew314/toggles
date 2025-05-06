package dev.andrewohara.toggles.http.client

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.http.ProjectCreateDataDto
import dev.andrewohara.toggles.http.ProjectDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.ToggleCreateDataDto
import dev.andrewohara.toggles.http.ToggleDto
import dev.andrewohara.toggles.http.ToggleUpdateDataDto
import dev.andrewohara.toggles.http.TogglesRoutes
import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.http.TogglesPageDto
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.with
import org.http4k.lens.BodyLens
import org.http4k.lens.LensFailure
import org.http4k.lens.bearerAuth

class TogglesHttpClient(
    private val host: Uri,
    private val idToken: String,
    private val internet: HttpHandler = JavaHttpClient()
) {
    fun listProjects(cursor: ProjectName? = null) = TogglesRoutes
        .listProjects(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(TogglesRoutes.projectCursorLens of cursor)
        .let(internet)
        .toResult(ProjectsPageDto.lens)

    fun createProject(data: ProjectCreateDataDto) = TogglesRoutes
        .createProject(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(ProjectCreateDataDto.lens of data)
        .let(internet)
        .toResult(ProjectDto.lens)

    fun deleteProject(projectName: ProjectName) = TogglesRoutes
        .deleteProject(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(TogglesRoutes.projectNameLens of projectName)
        .let(internet)
        .toResult(ProjectDto.lens)

    fun listToggles(projectName: ProjectName, cursor: ToggleName? = null) = TogglesRoutes
        .listToggles(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(TogglesRoutes.projectNameLens of projectName)
        .with(TogglesRoutes.toggleCursorLens of cursor)
        .let(internet)
        .toResult(TogglesPageDto.lens)

    fun createToggle(projectName: ProjectName, data: ToggleCreateDataDto) = TogglesRoutes
        .createToggle(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(TogglesRoutes.projectNameLens of projectName)
        .with(ToggleCreateDataDto.lens of data)
        .let(internet)
        .toResult(ToggleDto.lens)

    fun updateToggle(projectName: ProjectName, toggleName: ToggleName, data: ToggleUpdateDataDto) = TogglesRoutes
        .updateToggle(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(TogglesRoutes.projectNameLens of projectName)
        .with(TogglesRoutes.toggleNameLens of toggleName)
        .with(ToggleUpdateDataDto.lens of data)
        .let(internet)
        .toResult(ToggleDto.lens)

    fun getToggle(projectName: ProjectName, toggleName: ToggleName) = TogglesRoutes
        .getToggle(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(TogglesRoutes.projectNameLens of projectName)
        .with(TogglesRoutes.toggleNameLens of toggleName)
        .let(internet)
        .toResult(ToggleDto.lens)

    fun deleteToggle(projectName: ProjectName, toggleName: ToggleName) = TogglesRoutes
        .deleteToggle(null)
        .newRequest(host)
        .bearerAuth(idToken)
        .with(TogglesRoutes.projectNameLens of projectName)
        .with(TogglesRoutes.toggleNameLens of toggleName)
        .let(internet)
        .toResult(ToggleDto.lens)
}

private fun <Out: Any> Response.toResult(lens: BodyLens<Out>): Result4k<Out, TogglesErrorDto> = when(status) {
    Status.OK -> lens(this).asSuccess()
    Status.NOT_FOUND, Status.CONFLICT -> try {
        TogglesErrorDto.lens(this).asFailure()
    } catch (_: LensFailure) {
        TogglesErrorDto("Unhandled error: $this").asFailure()
    }
    else -> TogglesErrorDto("Unhandled error: $this").asFailure()
}
