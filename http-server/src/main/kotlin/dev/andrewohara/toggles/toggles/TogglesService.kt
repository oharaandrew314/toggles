package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.ToggleAlreadyExists
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.createUniqueId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.http.requireAdminOrDeveloper
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

@OptIn(ExperimentalStdlibApi::class)
private fun TogglesApp.createState(toggle: Toggle, environment: EnvironmentName) = ToggleState(
    uniqueId = createUniqueId(toggle.tenantId, toggle.projectName, toggle.toggleName),
    variations = toggle.environments[environment]?.weights ?: emptyMap(),
    overrides = toggle.environments[environment]?.overrides ?: emptyMap(),
    defaultVariation = toggle.defaultVariation,
)

fun TogglesApp.listToggles(tenantId: TenantId, projectName: ProjectName, cursor: ToggleName?) = storage
    .projects.getOrFail(tenantId, projectName)
    .map { storage.toggles.list(tenantId, projectName, pageSize)[cursor] }

fun TogglesApp.getToggle(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName) = storage
    .projects.getOrFail(tenantId, projectName)
    .flatMap { storage.toggles.getOrFail(tenantId, projectName, toggleName) }

fun TogglesApp.getState(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName, environmentName: EnvironmentName) = storage
    .projects.getOrFail(tenantId, projectName)
    .flatMap { storage.toggles.getOrFail(tenantId, projectName, toggleName) }
    .map { createState(it,environmentName) }

fun TogglesApp.createToggle(principal: User, projectName: ProjectName, data: ToggleCreateData) = principal
    .requireAdminOrDeveloper()
    .flatMap { storage.projects.getOrFail(principal.tenantId, projectName) }
    .failIf({ storage.toggles[principal.tenantId, projectName, data.toggleName] != null}, { ToggleAlreadyExists(projectName, data.toggleName) })
    .map { data.toToggle(principal.tenantId, projectName, clock.instant()) }
    .peek(storage.toggles::plusAssign)

fun TogglesApp.updateToggle(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName, data: ToggleUpdateData) = storage
    .projects.getOrFail(tenantId, projectName)
    .flatMap { storage.toggles.getOrFail(tenantId, projectName, toggleName) }
    .map { it.update(data, clock.instant()) }
    .peek(storage.toggles::plusAssign)

fun TogglesApp.deleteToggle(principal: User, projectName: ProjectName, toggleName: ToggleName) = principal
    .requireAdminOrDeveloper()
    .flatMap { storage.projects.getOrFail(principal.tenantId, projectName) }
    .flatMap { storage.toggles.getOrFail(principal.tenantId, projectName, toggleName) }
    .peek(storage.toggles::minusAssign)
