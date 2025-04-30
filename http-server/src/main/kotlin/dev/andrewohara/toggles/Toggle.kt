package dev.andrewohara.toggles

import java.time.Instant

data class Toggle(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val environments: Map<Environment, ToggleState>
): Comparable<Toggle> {
    override fun compareTo(other: Toggle) = when(val res = projectName.compareTo(other.projectName)) {
        0 -> toggleName.compareTo(other.toggleName)
        else -> res
    }
}
