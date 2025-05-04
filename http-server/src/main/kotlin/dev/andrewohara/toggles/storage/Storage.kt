package dev.andrewohara.toggles.storage

class Storage(
    val projects: ProjectStorage,
    val toggles: ToggleStorage
) {
    companion object
}