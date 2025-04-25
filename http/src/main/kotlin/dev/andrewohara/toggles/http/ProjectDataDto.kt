package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.ProjectName

data class ProjectDataDto(
    val projectName: ProjectName
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectDataDto>().toLens()

        val sample = ProjectDataDto(
            projectName = ProjectName.of("my_project")
        )
    }
}