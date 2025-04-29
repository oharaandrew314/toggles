package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract

class InMemoryProjectStorageTest: ProjectStorageContract() {
    override fun createProjectRepo() = ProjectStorage.inMemory()
}