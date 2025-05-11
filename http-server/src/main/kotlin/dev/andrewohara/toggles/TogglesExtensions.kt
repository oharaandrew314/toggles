package dev.andrewohara.toggles

import dev.andrewohara.toggles.UniqueId.Companion.parse
import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.toggles.getState
import dev.forkhandles.result4k.mapFailure
import java.security.MessageDigest
import java.util.Base64

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

private val base64 = Base64.getEncoder()

fun TogglesApp.createUniqueId(vararg seeds: Any) = MessageDigest.getInstance("MD5").run {
    update(secretKey)
    for (seed in seeds) { update(seed.toString().toByteArray())}
    parse(base64.encodeToString(digest()).take(UniqueId.LENGTH))
}