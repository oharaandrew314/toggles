package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ToggleStorageContract

class InMemoryToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = ToggleStorage.inMemory()
}