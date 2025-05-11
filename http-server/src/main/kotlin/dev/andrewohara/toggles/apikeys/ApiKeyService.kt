package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.ApiKey
import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.TogglesApp
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek
import java.security.MessageDigest

@OptIn(ExperimentalStdlibApi::class)
fun TogglesApp.hash(apiKey: ApiKey) = MessageDigest.getInstance("SHA-256").run {
    update(secretKey)
    val bytes = digest(apiKey.value.toByteArray())
    ApiKeyHash.parse(bytes.toHexString())
}

fun TogglesApp.generateApiKey(tenantId: TenantId, projectName: ProjectName, environmentName: EnvironmentName) = storage
    .projects.getOrFail(tenantId, projectName)
    .map { ApiKeyMeta(tenantId, projectName, environmentName, clock.instant()) to ApiKey.random(random) }
    .peek { (meta, key) -> storage.apiKeys[meta] = hash(key) }