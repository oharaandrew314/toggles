package dev.andrewohara.toggles

import dev.andrewohara.toggles.apikeys.ApiKeys
import dev.andrewohara.toggles.storage.ToggleStorage
import dev.andrewohara.toggles.storage.inMemory
import dev.andrewohara.utils.jdk.toClock
import java.time.Clock
import java.time.Instant
import kotlin.random.Random

interface TogglesFactory {
    fun createToggles(
        clock: Clock = Instant.parse("2025-04-27T12:00:00Z").toClock(),
        pageSize: Int = 2,
        random: Random = Random(1337)
    ): Toggles
}

interface InMemoryTogglesFactory: TogglesFactory {
    override fun createToggles(clock: Clock, pageSize: Int, random: Random) = Toggles(
        clock = clock,
        pageSize = pageSize,
        storage = ToggleStorage.inMemory(),
        random = random,
        apiKeys = ApiKeys {
            TODO()
        }
    )
}
