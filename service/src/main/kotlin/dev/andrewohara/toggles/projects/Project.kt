package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import java.time.Instant

data class Project(
    val tenantId: TenantId,
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