package dev.andrewohara.toggles.users.http

import dev.andrewohara.toggles.UniqueId
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

object UserRoutes {
    val idLens = Path.value(UniqueId).of("user_id")
    val cursorLens = Query.value(UniqueId).optional("cursor")
    val tag = Tag("Users")

    fun listUsers(auth: Security?) = "/v1/users" meta {
        operationId = "v1ListUsers"
        summary = "List Users"
        tags += tag
        security = auth

        queries += cursorLens
        returning(Status.OK, UserPageDto.lens to UserPageDto.sample)
    } bindContract Method.GET

    fun getUser(auth: Security?) = "/v1/users" / idLens meta {
        operationId = "v1GetUser"
        summary = "Get User"
        tags += tag
        security = auth

        returning(Status.OK, UserDto.lens to UserDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.userNotFoundSample)
    } bindContract Method.GET

    fun inviteUser(auth: Security?) = "/v1/users" meta {
        operationId = "v1InviteUser"
        summary = "Invite User"
        tags += tag
        security = auth

        receiving(UserInviteDataDto.lens to UserInviteDataDto.sample)
        returning(Status.OK, UserDto.lens to UserDto.sample)
        returning(Status.CONFLICT, TogglesErrorDto.lens to TogglesErrorDto.userAlreadyExistsSample)
    } bindContract Method.POST

    fun deleteUser(auth: Security?) = "/v1/users" / idLens meta {
        operationId = "v1DeleteUser"
        summary = "Delete User"
        tags += tag
        security = auth

        returning(Status.OK, UserDto.lens to UserDto.sample)
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.userNotFoundSample)
    } bindContract Method.DELETE

    fun updatePermissions(auth: Security?) = "/v1/users" / idLens / "permissions" meta {
        operationId = "v1UpdatePermissions"
        summary = "Update User Permissions"
        tags += tag
        security = auth

        receiving(UserPermissionsDataDto.lens to UserPermissionsDataDto.sample)
        returning(Status.OK, UserDto.lens to UserDto.sample)
        returning(Status.FORBIDDEN to "cannot change this user's permissions")
        returning(Status.NOT_FOUND, TogglesErrorDto.lens to TogglesErrorDto.userNotFoundSample)
    } bindContract Method.PUT
}