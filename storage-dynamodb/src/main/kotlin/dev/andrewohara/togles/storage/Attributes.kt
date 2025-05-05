package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.ToggleName
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.value
import org.http4k.lens.BiDiMapping

internal typealias ProjectRef = Pair<TenantId, ProjectName>

internal val projectRefMapping = BiDiMapping(ProjectRef::class.java,
    asOut = { text: String ->
        val (tenantId, projectId) = text.split("/")
        TenantId.parse(tenantId) to ProjectName.parse(projectId)
    },
    asIn = { (tenantId, projectId) -> "${tenantId.value}/${projectId.value}" }
)

internal val TenantId.Companion.attribute get() = Attribute.value(TenantId).required("tenantId")
internal val ProjectName.Companion.attribute get() = Attribute.value(ProjectName).required("projectName")
internal val projectRefAttr = Attribute.string().map(projectRefMapping).required("projectRef")
internal val ToggleName.Companion.attribute get() = Attribute.value(ToggleName).required("toggleName")
internal val EnvironmentName.Companion.attribute get() = Attribute.value(EnvironmentName).required("environmentName")