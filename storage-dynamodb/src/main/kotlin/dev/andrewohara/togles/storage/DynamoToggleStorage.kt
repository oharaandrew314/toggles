package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.toggles.Toggle
import dev.andrewohara.toggles.toggles.ToggleEnvironment
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.toggles.ToggleStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.model.Key
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

internal fun dynamoToggleStorage(
    toggles: DynamoDbTableMapper<DynamoToggle, ProjectRef, ToggleName>
) = object : ToggleStorage {

    override fun list(
        tenantId: TenantId,
        projectName: ProjectName,
        pageSize: Int
    ) = Paginator<Toggle, ToggleName> { cursor ->
        val page = toggles.primaryIndex().queryPage(
            HashKey = ProjectRef(tenantId, projectName),
            Limit = pageSize,
            ExclusiveStartKey = cursor?.let {
                Key(
                    projectRefAttr of ProjectRef(tenantId, projectName),
                    ToggleName.attribute of cursor
                )
            }
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(ToggleName.attribute)
        )
    }

    override fun get(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName) =
        toggles[ProjectRef(tenantId, projectName), toggleName]?.toModel()

    override fun plusAssign(toggle: Toggle) = toggles.plusAssign(toggle.toDynamo())

    override fun minusAssign(toggle: Toggle) = toggles.delete(
        hashKey = ProjectRef(toggle.tenantId, toggle.projectName),
        sortKey = toggle.toggleName
    )
}

@JsonSerializable
internal data class DynamoToggle(
    val projectRef: String,
    val toggleName: ToggleName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: List<VariationName>,
    val defaultVariation: VariationName,
    val environments: Map<EnvironmentName, DynamoEnvironment>
)

@JsonSerializable
internal data class DynamoEnvironment(
    val overrides: Map<SubjectId, VariationName>,
    val variations: Map<VariationName, Weight>
)

private fun DynamoToggle.toModel() = Toggle(
    tenantId = projectRefMapping(projectRef).tenantId,
    projectName = projectRefMapping(projectRef).projectName,
    toggleName = toggleName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { (_, env) ->
        ToggleEnvironment(env.variations, env.overrides)
    }
)

private fun Toggle.toDynamo() = DynamoToggle(
    projectRef = projectRefMapping(ProjectRef(tenantId, projectName)),
    toggleName = toggleName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    defaultVariation = defaultVariation,
    environments = environments.mapValues { (_, env) ->
        DynamoEnvironment(env.overrides, env.weights)
    }
)