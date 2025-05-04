package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleEnvironment
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
internal data class DynamoToggle(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val uniqueId: UniqueId,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, DynamoEnvironment>
)

@JsonSerializable
internal data class DynamoEnvironment(
    val overrides: Map<SubjectId, VariationName>,
    val variations: Map<VariationName, Weight>
)

internal fun DynamoToggle.toModel() = Toggle(
    projectName = projectName,
    toggleName = toggleName,
    uniqueId = uniqueId,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { (_, env) ->
        ToggleEnvironment(env.variations, env.overrides)
    }
)

internal fun Toggle.toDynamo() = DynamoToggle(
    projectName = projectName,
    toggleName = toggleName,
    uniqueId = uniqueId,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { (_, env) ->
        DynamoEnvironment(env.overrides, env.weights)
    }
)