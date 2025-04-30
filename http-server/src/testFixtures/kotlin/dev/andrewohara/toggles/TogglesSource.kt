package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.ToggleStorage
import dev.andrewohara.toggles.storage.inMemory
import dev.andrewohara.utils.jdk.toClock
import java.time.Clock
import java.time.Instant

interface TogglesSource {
    fun createToggles(
        clock: Clock = Instant.parse("2025-04-27T12:00:00Z").toClock(),
        pageSize: Int = 2
    ): Toggles
}

interface InMemoryTogglesSource: TogglesSource {
    override fun createToggles(clock: Clock, pageSize: Int) = Toggles(
        clock = clock,
        pageSize = pageSize,
        storage = ToggleStorage.inMemory()
    )
}
