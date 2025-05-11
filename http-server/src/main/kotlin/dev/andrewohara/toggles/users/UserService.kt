package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.UniqueId
import dev.forkhandles.result4k.map


fun TogglesApp.listUsers(tenantId: TenantId, cursor: UniqueId?) = storage
    .tenants.getOrFail(tenantId)
    .map { storage.users.list(tenantId, pageSize)[cursor] }