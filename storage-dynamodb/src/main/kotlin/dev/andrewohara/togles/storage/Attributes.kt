package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.value

internal val ProjectName.Companion.attribute get() = Attribute.value(ProjectName).required("projectName")
internal val ToggleName.Companion.attribute get() = Attribute.value(ToggleName).required("toggleName")