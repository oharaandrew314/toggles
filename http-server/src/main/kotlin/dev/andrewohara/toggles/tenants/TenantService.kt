package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.UserAlreadyExists
import dev.andrewohara.toggles.createUniqueId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.UserRole
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

fun TogglesApp.createTenant(data: TenantCreateData) = begin
    .map { Tenant(TenantId.random(random), clock.instant()) }
    .peek(storage.tenants::plusAssign)
    .peek { storage.users += User(it.tenantId, createUniqueId(data.ownerEmailAddress), data.ownerEmailAddress, clock.instant(),UserRole.Admin) }

// TODO ensure user doesn't already exist
fun TogglesApp.inviteUser(tenantId: TenantId, emailAddress: EmailAddress, role: UserRole) = begin
    .failIf({ storage.users[emailAddress] != null }, { UserAlreadyExists(tenantId, emailAddress) })
    .map { User(tenantId, createUniqueId(emailAddress), emailAddress, clock.instant(), role) }
    .peek(storage.users::plusAssign)

// TODO authorization
fun TogglesApp.updateUserRole(tenantId: TenantId, userId: UniqueId, role: UserRole) = storage
    .users.getOrFail(tenantId, userId)
    .map { it.copy(role = role) }
    .peek(storage.users::plusAssign)

// TODO authorization
// TODO can't delete last admin of tenant; need to delete tenant instead
fun TogglesApp.removeUser(tenantId: TenantId, userId: UniqueId) = storage
    .users.getOrFail(tenantId, userId)
    .peek(storage.users::minusAssign)

// TODO delete tenant; requires all projects and users to be deleted


