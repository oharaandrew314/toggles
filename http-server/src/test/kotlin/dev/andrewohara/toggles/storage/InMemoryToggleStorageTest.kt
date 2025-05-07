package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.apikeys.ApiKeysStorageContract
import dev.andrewohara.toggles.projects.ProjectStorageContract
import dev.andrewohara.toggles.Storage
import dev.andrewohara.toggles.toggles.ToggleStorageContract
import dev.andrewohara.toggles.inMemory
import dev.andrewohara.toggles.tenants.TenantStorageContract
import dev.andrewohara.toggles.users.UserStorageContract

class InMemoryToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryApiKeyStorageTest: ApiKeysStorageContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryTenantStorageTest: TenantStorageContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryUserStorageTest: UserStorageContract() {
    override fun createStorage() = Storage.inMemory()
}