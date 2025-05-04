package dev.andrewohara.toggles

import java.time.Instant

data class Project(
    val projectName: ProjectName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val environments: List<EnvironmentName>
)

data class ProjectCreateData(
    val projectName: ProjectName,
    val environments: List<EnvironmentName>
)

data class ProjectUpdateData(
    val environments: List<EnvironmentName>
)