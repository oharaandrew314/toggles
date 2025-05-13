package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.togglesJson
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
data class ProjectDto(
    val projectName: ProjectName,
    val environments: List<EnvironmentName>,
    val createdOn: Instant,
    val updatedOn: Instant
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectDto>().toLens()

        val sample = ProjectDto(
            projectName = ProjectName.Companion.of("my_project"),
            environments = listOf(EnvironmentName.Companion.of("dev"), EnvironmentName.Companion.of("prod")),
            createdOn = Instant.parse("2025-04-24T12:00:00Z"),
            updatedOn = Instant.parse("2025-04-25T12:00:00Z")
        )
    }
}