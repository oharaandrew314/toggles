package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.toggles.ToggleStorageContract

class InMemoryToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = Storage.inMemory()
}