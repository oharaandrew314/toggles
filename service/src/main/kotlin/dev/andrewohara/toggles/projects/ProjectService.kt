package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ProjectAlreadyExists
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotEmpty
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.requireAdminOrDeveloper
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

fun TogglesApp.createProject(principal: User, data: ProjectCreateData) = principal
    .requireAdminOrDeveloper()
    .failIf({ storage.projects[principal.tenantId, data.projectName] != null}, { ProjectAlreadyExists(data.projectName)})
    .map {
        val time = clock.instant()
        Project(principal.tenantId, data.projectName, time, time, data.environments)
    }
    .peek(storage.projects::plusAssign)

fun TogglesApp.updateProject(principal: User, projectName: ProjectName, data: ProjectUpdateData) = principal
    .requireAdminOrDeveloper()
    .flatMap { storage.projects.getOrFail(principal.tenantId, projectName) }
    .map { it.copy(environments = data.environments, updatedOn = clock.instant()) }
    .peek(storage.projects::plusAssign)

fun TogglesApp.listProjects(tenantId: TenantId, cursor: ProjectName?) =
    storage.projects.list(tenantId, pageSize)[cursor]

fun TogglesApp.deleteProject(principal: User, projectName: ProjectName) = principal
    .requireAdminOrDeveloper()
    .flatMap { storage.projects.getOrFail(principal.tenantId, projectName) }
    .failIf({storage.toggles.list(principal.tenantId, projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .failIf({storage.apiKeys.list(principal.tenantId, projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .peek(storage.projects::minusAssign)