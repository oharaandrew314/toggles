package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract

class InMemoryProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = ProjectStorage.inMemory()
}