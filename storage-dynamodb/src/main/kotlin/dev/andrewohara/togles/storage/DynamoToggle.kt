package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
internal data class DynamoToggle(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>,
    val defaultVariation: VariationName
)

internal fun DynamoToggle.toModel() = Toggle(
    projectName = projectName,
    toggleName = toggleName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    overrides = overrides,
    defaultVariation = defaultVariation
)

internal fun Toggle.toDynamo() = DynamoToggle(
    projectName = projectName,
    toggleName = toggleName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    overrides = overrides,
    defaultVariation = defaultVariation
)