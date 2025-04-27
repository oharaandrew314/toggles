package dev.andrewohara.toggles.source

import com.github.benmanes.caffeine.cache.Caffeine
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.Result4k
import java.time.Duration

/**
 * Requires caffeine to be on the classpath
 */
fun ToggleSource.caffeineCached(
    duration: Duration = Duration.ofMinutes(1)
): ToggleSource {
    val cache = Caffeine.newBuilder()
        .expireAfterWrite(duration)
        .build<Pair<ProjectName, ToggleName>, Result4k<ToggleState, String>> { (projectName, toggleName) ->
            this(projectName, toggleName)
        }

    return ToggleSource { projectName, toggleName -> cache.get(projectName to toggleName) }
}