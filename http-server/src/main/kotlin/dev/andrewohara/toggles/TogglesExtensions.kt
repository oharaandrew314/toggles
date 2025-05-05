package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.toggles.getState
import dev.forkhandles.result4k.mapFailure

fun TogglesApp.toToggleSource(
    tenantId: TenantId,
    projectName: ProjectName,
    environment: EnvironmentName
) = object: ToggleSource {

    override fun invoke(toggleName: ToggleName) = this@toToggleSource
        .getState(tenantId, projectName, toggleName, environment)
        .mapFailure { it.toDto().message }

    override fun close() {}
}