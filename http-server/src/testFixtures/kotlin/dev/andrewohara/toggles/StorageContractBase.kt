package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.Storage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.time.Instant

abstract class StorageContractBase {

    protected val t0: Instant = Instant.parse("2025-04-29T12:00:00Z")

    protected lateinit var storage: Storage
    abstract fun createStorage(): Storage

    @BeforeEach
    open fun setup() {
        storage = createStorage()
    }

    @AfterEach
    fun clear() = with(storage) {
        for (project in projects.list(100)) {
            for (key in apiKeys.list(project.projectName, 100)) {
                apiKeys -= key
            }
            for (toggle in toggles.list(project.projectName, 100)) {
                toggles.remove(toggle.projectName, toggle.toggleName)
            }

            projects -= project.projectName
        }
    }
}