package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import org.http4k.contract.Tag
import java.time.Instant

data class ToggleDto(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>,
    val defaultVariation: VariationName
) {
    companion object {
        val lens = togglesJson.autoBody<ToggleDto>().toLens()
        val manyLens = togglesJson.autoBody<Array<ToggleDto>>().toLens()

        val tag = Tag("Toggles")

        val sample = ToggleDto(
            projectName = ProjectName.of("my_project"),
            toggleName = ToggleName.of("my_toggle"),
            variations = ToggleUpdateDataDto.sample.variations,
            defaultVariation = ToggleUpdateDataDto.sample.defaultVariation,
            overrides = ToggleUpdateDataDto.sample.overrides,
            createdOn = Instant.parse("2025-04-24T12:00:00Z"),
            updatedOn = Instant.parse("2025-04-25T12:00:00Z")
        )
    }
}
