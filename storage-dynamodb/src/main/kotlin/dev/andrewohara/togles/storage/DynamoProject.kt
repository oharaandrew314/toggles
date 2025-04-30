package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
internal data class DynamoProject(
    val projectName: ProjectName,
    val createdOn: Instant
)

internal fun DynamoProject.toModel() = Project(
    projectName = projectName,
    createdOn = createdOn
)

internal fun Project.toDynamo() = DynamoProject(
    projectName = projectName,
    createdOn = createdOn
)