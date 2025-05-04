package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProjectUpdateDataDto(
    val environments: List<EnvironmentName>
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectUpdateDataDto>().toLens()

        val sample = ProjectUpdateDataDto(
            environments = listOf(EnvironmentName.of("production"))
        )
    }
}