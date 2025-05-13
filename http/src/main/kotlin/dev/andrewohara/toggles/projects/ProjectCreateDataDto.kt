package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.togglesJson
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProjectCreateDataDto(
    val projectName: ProjectName,
    val environments: List<EnvironmentName>
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectCreateDataDto>().toLens()

        val sample = ProjectCreateDataDto(
            projectName = ProjectName.Companion.of("my_project"),
            environments = listOf(EnvironmentName.Companion.of("production"))
        )
    }
}