package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.togglesJson
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProjectsPageDto(
    val items: List<ProjectDto>,
    val next: ProjectName?
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectsPageDto>().toLens()
        val sample = ProjectsPageDto(
            items = listOf(ProjectDto.sample),
            next = ProjectName.Companion.of("my_project")
        )
    }
}