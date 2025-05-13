package dev.andrewohara.toggles

import dev.andrewohara.toggles.apikeys.ApiKeyStorage
import dev.andrewohara.toggles.apikeys.apiKeyStorage
import dev.andrewohara.toggles.projects.ProjectStorage
import dev.andrewohara.toggles.projects.inMemoryProjectStorage
import dev.andrewohara.toggles.tenants.TenantStorage
import dev.andrewohara.toggles.tenants.inMemoryTenantStorage
import dev.andrewohara.toggles.toggles.ToggleStorage
import dev.andrewohara.toggles.toggles.inMemoryToggleStorage
import dev.andrewohara.toggles.users.UserStorage
import dev.andrewohara.toggles.users.inMemoryUserStorage

class Storage(
    val projects: ProjectStorage,
    val toggles: ToggleStorage,
    val apiKeys: ApiKeyStorage,
    val tenants: TenantStorage,
    val users: UserStorage
) {
    companion object
}

fun Storage.Companion.inMemory() = Storage(
    projects = inMemoryProjectStorage(),
    toggles = inMemoryToggleStorage(),
    apiKeys = apiKeyStorage(),
    tenants = inMemoryTenantStorage(),
    users = inMemoryUserStorage()
)