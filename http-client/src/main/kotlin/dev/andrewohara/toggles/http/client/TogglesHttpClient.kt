package dev.andrewohara.toggles.http.client

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.projects.ProjectCreateDataDto
import dev.andrewohara.toggles.projects.ProjectDto
import dev.andrewohara.toggles.projects.ProjectsPageDto
import dev.andrewohara.toggles.toggles.ToggleCreateDataDto
import dev.andrewohara.toggles.toggles.ToggleDto
import dev.andrewohara.toggles.toggles.ToggleUpdateDataDto
import dev.andrewohara.toggles.TogglesErrorDto
import dev.andrewohara.toggles.toggles.TogglesPageDto
import dev.andrewohara.toggles.projects.ProjectRoutes
import dev.andrewohara.toggles.toggles.ToggleRoutes
import dev.andrewohara.toggles.users.UserDto
import dev.andrewohara.toggles.users.UserInviteDataDto
import dev.andrewohara.toggles.users.UserPageDto
import dev.andrewohara.toggles.users.UserPermissionsDataDto
import dev.andrewohara.toggles.users.UserRoutes
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters
import org.http4k.lens.BodyLens
import org.http4k.lens.LensFailure

class TogglesHttpClient(
    host: Uri,
    idToken: String,
    internet: HttpHandler = JavaHttpClient()
) {
    private val http = ClientFilters.SetHostFrom(host)
        .then(ClientFilters.BearerAuth(idToken))
        .then(internet)

    // projects

    fun listProjects(cursor: ProjectName? = null) = ProjectRoutes
        .listProjects(null)
        .newRequest()
        .with(ProjectRoutes.projectCursorLens of cursor)
        .let(http)
        .toResult(ProjectsPageDto.lens)

    fun createProject(data: ProjectCreateDataDto) = ProjectRoutes
        .createProject(null)
        .newRequest()
        .with(ProjectCreateDataDto.lens of data)
        .let(http)
        .toResult(ProjectDto.lens)

    fun deleteProject(projectName: ProjectName) = ProjectRoutes
        .deleteProject(null)
        .newRequest()
        .with(ProjectRoutes.projectNameLens of projectName)
        .let(http)
        .toResult(ProjectDto.lens)

    // toggles

    fun listToggles(projectName: ProjectName, cursor: ToggleName? = null) = ToggleRoutes
        .listToggles(null)
        .newRequest()
        .with(ProjectRoutes.projectNameLens of projectName)
        .with(ToggleRoutes.toggleCursorLens of cursor)
        .let(http)
        .toResult(TogglesPageDto.lens)

    fun createToggle(projectName: ProjectName, data: ToggleCreateDataDto) = ToggleRoutes
        .createToggle(null)
        .newRequest()
        .with(ProjectRoutes.projectNameLens of projectName)
        .with(ToggleCreateDataDto.lens of data)
        .let(http)
        .toResult(ToggleDto.lens)

    fun updateToggle(projectName: ProjectName, toggleName: ToggleName, data: ToggleUpdateDataDto) = ToggleRoutes
        .updateToggle(null)
        .newRequest()
        .with(ProjectRoutes.projectNameLens of projectName)
        .with(ToggleRoutes.toggleNameLens of toggleName)
        .with(ToggleUpdateDataDto.lens of data)
        .let(http)
        .toResult(ToggleDto.lens)

    fun getToggle(projectName: ProjectName, toggleName: ToggleName) = ToggleRoutes
        .getToggle(null)
        .newRequest()
        .with(ProjectRoutes.projectNameLens of projectName)
        .with(ToggleRoutes.toggleNameLens of toggleName)
        .let(http)
        .toResult(ToggleDto.lens)

    fun deleteToggle(projectName: ProjectName, toggleName: ToggleName) = ToggleRoutes
        .deleteToggle(null)
        .newRequest()
        .with(ProjectRoutes.projectNameLens of projectName)
        .with(ToggleRoutes.toggleNameLens of toggleName)
        .let(http)
        .toResult(ToggleDto.lens)

    // users

    fun inviteUser(data: UserInviteDataDto) = UserRoutes
        .inviteUser(null)
        .newRequest()
        .with(UserInviteDataDto.lens of data)
        .let(http)
        .toResult(UserDto.lens)

    fun listUsers(cursor: UniqueId? = null) = UserRoutes
        .listUsers(null)
        .newRequest()
        .with(UserRoutes.cursorLens of cursor)
        .let(http)
        .toResult(UserPageDto.lens)

    fun getUser(userId: UniqueId) = UserRoutes
        .getUser(null)
        .newRequest()
        .with(UserRoutes.idLens of userId)
        .let(http)
        .toResult(UserDto.lens)

    fun updatePermissions(userId: UniqueId, data: UserPermissionsDataDto) = UserRoutes
        .updatePermissions(null)
        .newRequest()
        .with(UserRoutes.idLens of userId)
        .with(UserPermissionsDataDto.lens of data)
        .let(http)
        .toResult(UserDto.lens)

    fun deleteUser(userId: UniqueId) = UserRoutes
        .deleteUser(null)
        .newRequest()
        .with(UserRoutes.idLens of userId)
        .let(http)
        .toResult(UserDto.lens)
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
