package dev.andrewohara.toggles

import java.time.Duration
import dev.forkhandles.result4k.Result4k
import com.github.benmanes.caffeine.cache.Caffeine

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

