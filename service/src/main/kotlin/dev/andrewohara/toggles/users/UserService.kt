package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.UserAlreadyExists
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

fun TogglesApp.listUsers(tenantId: TenantId, cursor: UniqueId?) = storage
    .tenants.getOrFail(tenantId)
    .map { storage.users.list(tenantId, pageSize)[cursor] }

fun TogglesApp.getUser(tenantId: TenantId, userId: UniqueId) =
    storage.users.getOrFail(tenantId, userId)

fun TogglesApp.getUser(emailAddress: EmailAddress) = storage.users.getOrFail(emailAddress)

fun TogglesApp.inviteUser(principal: User, emailAddress: EmailAddress, role: UserRole) = principal
    .requireAdmin()
    .flatMap {
        storage.users[emailAddress]
            ?.let { UserAlreadyExists(it.tenantId, it.emailAddress).asFailure() }
            ?: Unit.asSuccess()
    }
    .map { User(principal.tenantId, createUniqueId(emailAddress), emailAddress, clock.instant(), role) }
    .peek(storage.users::plusAssign)

fun TogglesApp.updateUserRole(principal: User, userId: UniqueId, role: UserRole) = principal
    .requireAdmin()
    .flatMap { storage.users.getOrFail(principal.tenantId, userId) }
    .cannotBePrincipal(principal)
    .map { it.copy(role = role) }
    .peek(storage.users::plusAssign)

fun TogglesApp.deleteUser(principal: User, userId: UniqueId) = principal
    .requireAdmin()
    .flatMap { storage.users.getOrFail(principal.tenantId, userId) }
    .cannotBePrincipal(principal)
    .peek(storage.users::minusAssign)