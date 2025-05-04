package dev.andrewohara.toggles.source

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.peek
import dev.forkhandles.result4k.peekFailure
import mu.KLogger
import mu.KotlinLogging
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

fun ToggleSource.caching(
    refreshInternal: Duration = Duration.ofMinutes(1),
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    logger: KLogger = KotlinLogging.logger("toggle-source-caching")
): ToggleSource {
    val cache = ConcurrentHashMap<ToggleName, Result4k<ToggleState, String>>()

    fun refresh() {
        for (key in cache.keys) {
            try {
                cache[key] = invoke(key)
                    .peek { logger.debug { "Refreshed toggle: $key -> $it" } }
                    .peekFailure { error { "Failed to refresh toggle: $key -> $it" } }
            } catch (e: Exception) {
                logger.error(e) { "Error refreshing toggle: $key" }
            }
        }
    }

    scheduler.scheduleWithFixedDelay(::refresh, 0, refreshInternal.toMillis(), TimeUnit.MILLISECONDS)

    val inner = this

    return object: ToggleSource {
        override fun invoke(toggleName: ToggleName) = cache
            .getOrPut(toggleName) { inner(toggleName) }

        override fun close() = scheduler.shutdown()
    }
}