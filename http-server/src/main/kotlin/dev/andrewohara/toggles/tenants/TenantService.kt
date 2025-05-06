package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.UserAlreadyExists
import dev.andrewohara.toggles.UserId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.UserRole
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek
import java.security.MessageDigest

@OptIn(ExperimentalStdlibApi::class)
private fun TogglesApp.getUserId(emailAddress: EmailAddress) = MessageDigest.getInstance("MD5").run {
    update(secretKey)
    val bytes = digest(emailAddress.value.toByteArray())
    UserId.parse(bytes.toHexString())
}

fun TogglesApp.createTenant(data: TenantCreateData) = begin
    .map { Tenant(TenantId.random(random), clock.instant()) }
    .peek(storage.tenants::plusAssign)
    .peek { storage.users += User(it.tenantId, getUserId(data.ownerEmailAddress), data.ownerEmailAddress, clock.instant(),UserRole.Admin) }

// TODO ensure user doesn't already exist
fun TogglesApp.inviteUser(tenantId: TenantId, emailAddress: EmailAddress, role: UserRole) = begin
    .failIf({ storage.users.list(pageSize).any { it.emailAddress == emailAddress }}, { UserAlreadyExists(tenantId, emailAddress) })
    .map { User(tenantId, getUserId(emailAddress), emailAddress, clock.instant(), role) }
    .peek(storage.users::plusAssign)

fun TogglesApp.updateUserRole(tenantId: TenantId, userId: UserId, role: UserRole) = storage
    .users.getOrFail(tenantId, userId)
    .map { it.copy(role = role) }
    .peek(storage.users::plusAssign)

fun TogglesApp.removeUser(tenantId: TenantId, userId: UserId) = storage
    .users.getOrFail(tenantId, userId)
    .peek(storage.users::minusAssign)


