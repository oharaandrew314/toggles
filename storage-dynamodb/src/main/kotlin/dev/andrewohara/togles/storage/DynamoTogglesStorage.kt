package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.storage.TogglesStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.mapper.minusAssign
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

fun TogglesStorage.Companion.dynamoDb(
    table: DynamoDbTableMapper<DynamoToggle, ProjectName, ToggleName>
) = DynamoTogglesStorage(table)

fun TogglesStorage.Companion.dynamoDb(
    dynamoDb: DynamoDb, tableName: TableName
) = DynamoTogglesStorage(dynamoDb.tableMapper(tableName, DynamoTogglesStorage.primaryIndex))

class DynamoTogglesStorage internal constructor(
    private val table: DynamoDbTableMapper<DynamoToggle, ProjectName, ToggleName>
): TogglesStorage {
    companion object {
        val primaryIndex = DynamoDbTableMapperSchema.Primary<DynamoToggle, ProjectName, ToggleName>(
            hashKeyAttribute = ProjectName.attribute,
            sortKeyAttribute = ToggleName.attribute,
            lens = togglesJson.autoDynamoLens()
        )
    }

    override fun list(
        projectName: ProjectName,
        pageSize: Int
    ) =  Paginator<Toggle, ToggleName> { cursor ->
        val page = table.index(primaryIndex).queryPage(
            HashKey = projectName,
            ExclusiveStartKey = cursor?.let { Key(ProjectName.attribute of projectName, ToggleName.attribute of cursor) }
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(ToggleName.attribute)
        )
    }

    override fun get(projectName: ProjectName, toggleName: ToggleName) = table[projectName, toggleName]?.toModel()
    override fun plusAssign(toggle: Toggle) = table.plusAssign(toggle.toDynamo())
    override fun minusAssign(toggle: Toggle) = table.minusAssign(toggle.toDynamo())
    override fun delete(projectName: ProjectName, toggleName: ToggleName): Toggle? =
        get(projectName, toggleName)?.also(::minusAssign)
}

@JsonSerializable
data class DynamoToggle(
    val projectName: ProjectName,
    val toggleName: ToggleName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val variations: Map<VariationName, Weight>,
    val overrides: Map<SubjectId, VariationName>,
    val defaultVariation: VariationName
)

private fun DynamoToggle.toModel() = Toggle(
    projectName = projectName,
    toggleName = toggleName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    overrides = overrides,
    defaultVariation = defaultVariation
)

private fun Toggle.toDynamo() = DynamoToggle(
    projectName = projectName,
    toggleName = toggleName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    variations = variations,
    overrides = overrides,
    defaultVariation = defaultVariation
)