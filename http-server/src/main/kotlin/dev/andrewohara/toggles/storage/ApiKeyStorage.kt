package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ApiKeyNotFound
import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.TokenMd5
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ApiKeyStorage {
    fun list(projectName: ProjectName, pageSize: Int): Paginator<ApiKeyMeta, EnvironmentName>
    operator fun get(projectName: ProjectName, environment: EnvironmentName): ApiKeyMeta?
    operator fun set(meta: ApiKeyMeta, tokenMd5: TokenMd5)
    operator fun minusAssign(meta: ApiKeyMeta)
    operator fun get(tokenMd5: TokenMd5): ApiKeyMeta?
}

fun ApiKeyStorage.getOrFail(projectName: ProjectName, environment: EnvironmentName) =
    this[projectName, environment].asResultOr { ApiKeyNotFound(projectName, environment) }