package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.toResponse
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestLens
import org.http4k.security.Security

fun userApiV1(service: TogglesApp, security: Security, authLens: RequestLens<User>) = listOf(
    UserRoutes.listUsers(security) to { request: Request ->
        service.listUsers(authLens(request).tenantId, UserRoutes.cursorLens(request))
            .map { UserPageDto(it.items.map(User::toDto), it.next) }
            .map { Response(Status.OK).with(UserPageDto.lens of it) }
            .recover { it.toResponse() }
    },
    UserRoutes.getUser(security) to { userId ->
        { request ->
            service.getUser(authLens(request).tenantId, userId)
                .map { Response(Status.OK).with(UserDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    },
    UserRoutes.inviteUser(security) to { request: Request ->
        val data = UserInviteDataDto.lens(request)
        service.inviteUser(
            principal = authLens(request),
            emailAddress = data.emailAddress,
            role = data.permissions.role.toModel()
        )
            .map { Response(Status.OK).with(UserDto.lens of it.toDto()) }
            .recover { it.toResponse() }
    },
    UserRoutes.deleteUser(security) to { userId ->
        { request ->
            service.deleteUser(authLens(request), userId)
                .map { Response(Status.OK).with(UserDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    },
    UserRoutes.updatePermissions(security) to { userId, _ ->
        { request ->
            service.updateUserRole(
                principal = authLens(request),
                userId = userId,
                role = UserPermissionsDataDto.lens(request).role.toModel()
            )
                .map { Response(Status.OK).with(UserDto.lens of it.toDto()) }
                .recover { it.toResponse() }
        }
    }
)

fun User.toDto() = UserDto(
    userId = uniqueId,
    emailAddress = emailAddress,
    createdOn = createdOn,
    role = when (role) {
        UserRole.Admin -> UserRoleDto.Admin
        UserRole.Developer -> UserRoleDto.Developer
        UserRole.Tester -> UserRoleDto.Tester
    }
)

private fun UserRoleDto.toModel() = when(this) {
    UserRoleDto.Admin -> UserRole.Admin
    UserRoleDto.Developer -> UserRole.Developer
    UserRoleDto.Tester -> UserRole.Tester
}