package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import java.time.Instant

data class Toggle(
    val tenantId: TenantId,
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironment>
)

data class ToggleEnvironment(
    val weights: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>
)
