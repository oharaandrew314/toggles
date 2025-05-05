package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListMap

internal fun apiKeyStorage() = object: ApiKeyStorage {
    private val keys = ConcurrentSkipListMap<ApiKeyMeta, ApiKeyHash> { o1, o2 ->
        "${o1.projectName}/${o1.environment}".compareTo("${o2.projectName}/${o2.environment}")
    }

    override fun list(tenantId: TenantId, projectName: ProjectName, pageSize: Int) =
        Paginator<ApiKeyMeta, EnvironmentName> { cursor ->
            val page = keys
                .entries
                .filter { it.key.tenantId == tenantId && it.key.projectName == projectName }
                .map { it.key }
                .sortedBy { it.environment }
                .dropWhile { cursor != null && it.environment <= cursor }
                .take(pageSize + 1)

            Page(
                items = page.take(pageSize),
                next = page.drop(pageSize).firstOrNull()?.environment
            )
        }

    override fun get(tenantId: TenantId, projectName: ProjectName, environment: EnvironmentName) =
        keys.keys.find { it.tenantId == tenantId && it.projectName == projectName && it.environment == environment }

    override fun set(meta: ApiKeyMeta, apiKeyHash: ApiKeyHash) {
        keys[meta] = apiKeyHash
    }

    override fun minusAssign(meta: ApiKeyMeta) = keys.minusAssign(meta)

    override fun get(apiKeyHash: ApiKeyHash) = keys.entries
        .find { it.value == apiKeyHash }
        ?.key
}