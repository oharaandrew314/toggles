package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import org.http4k.format.Moshi
import org.http4k.format.value

val togglesJson = Moshi.custom {
    value(ProjectName)
    value(ToggleName)
    value(VariationName)
    value(Weight)
    value(SubjectId)
}