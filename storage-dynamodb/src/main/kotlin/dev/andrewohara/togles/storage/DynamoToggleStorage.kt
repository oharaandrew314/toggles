package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.storage.ToggleStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.flatMapFailure
import dev.forkhandles.result4k.onFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.describeTable
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema.Primary
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens

internal fun dynamoToggleStorage(dynamoDb: DynamoDb, tableName: TableName, autoCreate: Boolean): ToggleStorage {
    val toggles = dynamoDb.tableMapper(
        tableName, Primary<DynamoToggle, ProjectName, ToggleName>(
            hashKeyAttribute = ProjectName.attribute,
            sortKeyAttribute = ToggleName.attribute,
            lens = togglesJson.autoDynamoLens()
        )
    )

    dynamoDb.describeTable(tableName)
        .flatMapFailure { if (autoCreate) toggles.createTable() else it.asFailure() }
        .onFailure { it.reason.throwIt() }

    return object : ToggleStorage {
        override fun list(
            projectName: ProjectName,
            pageSize: Int
        ) = Paginator<Toggle, ToggleName> { cursor ->
            val page = toggles.primaryIndex().queryPage(
                HashKey = projectName,
                Limit = pageSize,
                ExclusiveStartKey = cursor?.let {
                    Key(
                        ProjectName.attribute of projectName,
                        ToggleName.attribute of cursor
                    )
                }
            )

            Page(
                items = page.items.map { it.toModel() },
                next = page.lastEvaluatedKey?.let(ToggleName.attribute)
            )
        }

        override fun get(projectName: ProjectName, toggleName: ToggleName) =
            toggles[projectName, toggleName]?.toModel()

        override fun plusAssign(toggle: Toggle) = toggles.plusAssign(toggle.toDynamo())

        override fun remove(projectName: ProjectName, toggleName: ToggleName) =
            toggles.delete(projectName, toggleName)
    }
}