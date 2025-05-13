package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import java.time.Instant

data class ApiKeyMeta(
    val tenantId: TenantId,
    val projectName: ProjectName,
    val environment: EnvironmentName,
    val createdOn: Instant
)