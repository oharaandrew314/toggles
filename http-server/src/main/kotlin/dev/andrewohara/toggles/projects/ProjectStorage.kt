package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotFound
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ProjectStorage {
    fun list(tenantId: TenantId, pageSize: Int): Paginator<Project, ProjectName>
    operator fun get(tenantId: TenantId, projectName: ProjectName): Project?
    operator fun plusAssign(project: Project)
    operator fun minusAssign(project: Project)

    fun getOrFail(tenantId: TenantId, projectName: ProjectName) =
        get(tenantId, projectName).asResultOr { ProjectNotFound(projectName) }
}