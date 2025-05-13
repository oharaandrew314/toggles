package dev.andrewohara.toggles.toggles.http

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.http.togglesJson
import org.http4k.contract.Tag
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
data class ToggleDto(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, ToggleEnvironmentDto>,
) {
    companion object {
        val lens = togglesJson.autoBody<ToggleDto>().toLens()

        val sample = ToggleDto(
            projectName = ProjectName.of("my_project"),
            toggleName = ToggleName.of("my_toggle"),
            variations = listOf(VariationName.of("off"), VariationName.of("on")),
            defaultVariation = VariationName.of("off"),
            createdOn = Instant.parse("2025-04-24T12:00:00Z"),
            updatedOn = Instant.parse("2025-04-25T12:00:00Z"),
            environments = mapOf(
                EnvironmentName.of("development") to ToggleEnvironmentDto(
                    variations = mapOf(
                        VariationName.of("off") to Weight.of(0),
                        VariationName.of("on") to Weight.of(1)
                    ),
                    overrides = emptyMap()
                ),
                EnvironmentName.of("production") to ToggleEnvironmentDto(
                    variations = mapOf(
                        VariationName.of("off") to Weight.of(2),
                        VariationName.of("on") to Weight.of(1)
                    ),
                    overrides = mapOf(
                        SubjectId.of("testUser") to VariationName.of("on")
                    )
                )
            )
        )
    }
}
