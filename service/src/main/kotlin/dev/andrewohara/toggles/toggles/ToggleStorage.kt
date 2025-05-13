package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ToggleStorage {
    fun list(tenantId: TenantId, projectName: ProjectName, pageSize: Int): Paginator<Toggle, ToggleName>
    operator fun get(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName): Toggle?
    operator fun plusAssign(toggle: Toggle)
    operator fun minusAssign(toggle: Toggle)

    fun getOrFail(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName) =
        get(tenantId, projectName, toggleName).asResultOr { ToggleNotFound(projectName, toggleName) }
}