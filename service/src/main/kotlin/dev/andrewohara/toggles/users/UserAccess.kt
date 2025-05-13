package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.RequiresAdmin
import dev.andrewohara.toggles.RequiresAdminOrDeveloper
import dev.andrewohara.toggles.TogglesError
import dev.andrewohara.toggles.UserIsPrincipal
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap

fun User.requireAdmin() = if (role == UserRole.Admin) asSuccess() else RequiresAdmin.asFailure()

fun User.requireAdminOrDeveloper() = if (role == UserRole.Admin || role == UserRole.Developer) asSuccess() else RequiresAdminOrDeveloper.asFailure()

fun Result4k<User, TogglesError>.cannotBePrincipal(principal: User) = flatMap {
    if (principal.tenantId == it.tenantId && principal.uniqueId == it.uniqueId) {
        UserIsPrincipal.asFailure()
    } else {
        it.asSuccess()
    }
}