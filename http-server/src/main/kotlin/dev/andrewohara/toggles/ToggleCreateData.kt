package dev.andrewohara.toggles

import java.time.Instant
import kotlin.random.Random

data class ToggleCreateData(
    val toggleName: ToggleName,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironment>
)

fun ToggleCreateData.toToggle(
    projectName: ProjectName,
    time: Instant,
    random: Random
) = Toggle(
    projectName = projectName,
    toggleName = toggleName,
    uniqueId = UniqueId.random(random),
    createdOn = time,
    updatedOn = time,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments
)