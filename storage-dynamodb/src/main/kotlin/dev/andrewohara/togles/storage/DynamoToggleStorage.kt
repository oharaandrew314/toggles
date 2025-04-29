package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.storage.ToggleStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.flatMapFailure
import dev.forkhandles.result4k.onFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.describeTable
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema.Primary
import org.http4k.connect.amazon.dynamodb.mapper.minusAssign
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens
import se.ansman.kotshi.JsonSerializable
import java.time.Instant


fun ToggleStorage.Companion.dynamoDb(
    dynamoDb: DynamoDb, tableName: TableName, autoCreate: Boolean = false
) = DynamoToggleStorage(dynamoDb, tableName, autoCreate)

class DynamoToggleStorage internal constructor(
    dynamoDb: DynamoDb,
    tableName: TableName,
    private val autoCreate: Boolean = false
): ToggleStorage {

    private val table = dynamoDb.tableMapper(tableName, Primary<DynamoToggle, ProjectName, ToggleName>(
        hashKeyAttribute = ProjectName.attribute,
        sortKeyAttribute = ToggleName.attribute,
        lens = togglesJson.autoDynamoLens()
    ))

    init {
        dynamoDb.describeTable(tableName)
            .flatMapFailure { if (autoCreate) table.createTable() else it.asFailure() }
            .onFailure { it.reason.throwIt() }
    }

    override fun list(
        projectName: ProjectName,
        pageSize: Int
    ) =  Paginator<Toggle, ToggleName> { cursor ->
        val page = table.primaryIndex().queryPage(
            HashKey = projectName,
            Limit = pageSize,
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
internal data class DynamoToggle(
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