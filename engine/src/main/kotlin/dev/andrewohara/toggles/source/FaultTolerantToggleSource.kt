package dev.andrewohara.toggles.source

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.flatMapFailure
import dev.forkhandles.result4k.peek
import dev.forkhandles.result4k.peekFailure
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

fun ToggleSource.faultTolerant(): ToggleSource {
    val logger = KotlinLogging.logger { }
    val fallbacks = ConcurrentHashMap<Pair<ProjectName, ToggleName>, ToggleState>()

    return ToggleSource { projectName, toggleName ->
        this(projectName, toggleName)
            .peek { fallbacks[projectName to toggleName] = it }
            .peekFailure { logger.warn { "Failed to get $projectName/$toggleName: $it" } }
            .flatMapFailure { fallbacks[projectName to toggleName].asResultOr { it } }
    }
}