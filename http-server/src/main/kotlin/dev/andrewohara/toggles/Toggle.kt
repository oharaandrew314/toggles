package dev.andrewohara.toggles

import java.time.Instant

data class Toggle(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val uniqueId: UniqueId,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironment>
): Comparable<Toggle> {
    override fun compareTo(other: Toggle) = when(val res = projectName.compareTo(other.projectName)) {
        0 -> toggleName.compareTo(other.toggleName)
        else -> res
    }
}

data class ToggleEnvironment(
    val weights: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>
)
