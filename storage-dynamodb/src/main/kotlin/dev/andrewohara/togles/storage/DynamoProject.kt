package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
internal data class DynamoProject(
    val projectName: ProjectName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val environments: List<EnvironmentName>
)

internal fun DynamoProject.toModel() = Project(
    projectName = projectName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    environments = environments
)

internal fun Project.toDynamo() = DynamoProject(
    projectName = projectName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    environments = environments
)