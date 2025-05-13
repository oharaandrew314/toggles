package dev.andrewohara.toggles.projects.http

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.http.togglesJson
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProjectUpdateDataDto(
    val environments: List<EnvironmentName>
) {
    companion object {
        val lens = togglesJson.autoBody<ProjectUpdateDataDto>().toLens()

        val sample = ProjectUpdateDataDto(
            environments = listOf(EnvironmentName.Companion.of("production"))
        )
    }
}