package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.ProjectName

data class ProjectsPageDto(
    val items: List<ProjectDto>,
    val next: ProjectName?
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectsPageDto>().toLens()
        val sample = ProjectsPageDto(
            items = listOf(ProjectDto.sample),
            next = ProjectName.of("my_project")
        )
    }
}