package dev.andrewohara.toggles.storage

class Storage(
    val projects: ProjectStorage,
    val toggles: ToggleStorage,
    val apiKeys: ApiKeyStorage
) {
    companion object
}