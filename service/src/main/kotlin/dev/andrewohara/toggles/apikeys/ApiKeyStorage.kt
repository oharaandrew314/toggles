package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.ApiKeyNotFound
import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ApiKeyStorage {
    fun list(tenantId: TenantId, projectName: ProjectName, pageSize: Int): Paginator<ApiKeyMeta, EnvironmentName>
    operator fun get(tenantId: TenantId, projectName: ProjectName, environment: EnvironmentName): ApiKeyMeta?
    operator fun set(meta: ApiKeyMeta, apiKeyHash: ApiKeyHash)
    operator fun minusAssign(meta: ApiKeyMeta)
    operator fun get(apiKeyHash: ApiKeyHash): ApiKeyMeta?

    fun getOrFail(tenantId: TenantId, projectName: ProjectName, environment: EnvironmentName) =
        this[tenantId, projectName, environment].asResultOr { ApiKeyNotFound(projectName, environment) }
}