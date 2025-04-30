package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.ToggleStorage
import org.junit.jupiter.api.BeforeEach
import java.time.Instant

abstract class StorageContractBase {

    protected val t0: Instant = Instant.parse("2025-04-29T12:00:00Z")

    protected lateinit var storage: ToggleStorage
    abstract fun createStorage(): ToggleStorage

    @BeforeEach
    open fun setup() {
        storage = createStorage()
    }
}