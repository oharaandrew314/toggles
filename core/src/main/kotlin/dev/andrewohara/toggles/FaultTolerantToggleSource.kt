package dev.andrewohara.toggles

import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.flatMapFailure
import dev.forkhandles.result4k.peek
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

fun ToggleSource.faultTolerant(): ToggleSource {
    val fallbacks = ConcurrentHashMap<Pair<ProjectName, ToggleName>, ToggleState>()

    return ToggleSource { projectName, toggleName ->
        this(projectName, toggleName)
            .peek { fallbacks[projectName to toggleName] = it }
            .flatMapFailure { fallbacks[projectName to toggleName].asResultOr { it } }
    }
}