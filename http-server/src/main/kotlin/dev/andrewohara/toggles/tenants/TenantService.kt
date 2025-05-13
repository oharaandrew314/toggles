package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.createUniqueId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.UserRole
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

fun TogglesApp.createTenant(data: TenantCreateData) = begin
    .map { Tenant(TenantId.random(random), clock.instant()) }
    .peek(storage.tenants::plusAssign)
    .peek { storage.users += User(it.tenantId, createUniqueId(data.ownerEmailAddress), data.ownerEmailAddress, clock.instant(),UserRole.Admin) }

// TODO delete tenant; requires all projects and users to be deleted


