package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import java.time.Instant

data class ToggleCreateData(
    val toggleName: ToggleName,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironment>
)

fun ToggleCreateData.toToggle(
    tenantId: TenantId,
    projectName: ProjectName,
    time: Instant
) = Toggle(
    tenantId = tenantId,
    projectName = projectName,
    toggleName = toggleName,
    createdOn = time,
    updatedOn = time,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments
)