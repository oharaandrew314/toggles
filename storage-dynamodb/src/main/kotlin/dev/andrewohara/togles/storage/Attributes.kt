package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.UserId
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.value
import org.http4k.lens.BiDiMapping
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProjectRef(val tenantId: TenantId, val projectName: ProjectName)

internal val projectRefMapping = BiDiMapping(ProjectRef::class.java,
    asOut = { text: String ->
        val (tenantId, projectId) = text.split("/")
        ProjectRef(TenantId.parse(tenantId), ProjectName.parse(projectId))
    },
    asIn = { (tenantId, projectId) -> "${tenantId.value}/${projectId.value}" }
)

internal val TenantId.Companion.attribute get() = Attribute.value(TenantId).required("tenantId")
internal val ProjectName.Companion.attribute get() = Attribute.value(ProjectName).required("projectName")
internal val projectRefAttr = Attribute.string().map(projectRefMapping).required("projectRef")
internal val ToggleName.Companion.attribute get() = Attribute.value(ToggleName).required("toggleName")
internal val EnvironmentName.Companion.attribute get() = Attribute.value(EnvironmentName).required("environmentName")
internal val UserId.Companion.attribute get() = Attribute.value(UserId).required("userId")
internal val EmailAddress.Companion.attribute get() = Attribute.value(EmailAddress).required("emailAddress")