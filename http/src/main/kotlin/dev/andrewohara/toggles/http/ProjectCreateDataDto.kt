package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProjectCreateDataDto(
    val projectName: ProjectName,
    val environments: List<EnvironmentName>
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectCreateDataDto>().toLens()

        val sample = ProjectCreateDataDto(
            projectName = ProjectName.of("my_project"),
            environments = listOf(EnvironmentName.of("production"))
        )
    }
}