package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ProjectAlreadyExists
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotEmpty
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

fun TogglesApp.createProject(tenantId: TenantId, data: ProjectCreateData) = begin
    .failIf({ storage.projects[tenantId, data.projectName] != null}, { ProjectAlreadyExists(data.projectName)})
    .map {
        val time = clock.instant()
        Project(tenantId, data.projectName, time, time, data.environments)
    }
    .peek(storage.projects::plusAssign)

fun TogglesApp.updateProject(tenantId: TenantId, projectName: ProjectName, data: ProjectUpdateData) = storage
    .projects.getOrFail(tenantId, projectName)
    .map { it.copy(environments = data.environments, updatedOn = clock.instant()) }
    .peek(storage.projects::plusAssign)

fun TogglesApp.listProjects(tenantId: TenantId, cursor: ProjectName?) =
    storage.projects.list(tenantId, pageSize)[cursor]

fun TogglesApp.deleteProject(tenantId: TenantId, projectName: ProjectName) = storage
    .projects.getOrFail(tenantId, projectName)
    .failIf({storage.toggles.list(tenantId, projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .failIf({storage.apiKeys.list(tenantId,projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .peek(storage.projects::minusAssign)