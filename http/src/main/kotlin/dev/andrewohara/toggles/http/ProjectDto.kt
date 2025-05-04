package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import org.http4k.contract.Tag
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
        val manyLens = togglesJson.autoBody<Array<ProjectDto>>().toLens()

        val tag = Tag("Projects")

        val sample = ProjectDto(
            projectName = ProjectName.of("my_project"),
            environments = listOf(EnvironmentName.of("dev"), EnvironmentName.of("prod")),
            createdOn = Instant.parse("2025-04-24T12:00:00Z"),
            updatedOn = Instant.parse("2025-04-25T12:00:00Z")
        )
    }
}