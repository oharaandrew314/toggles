package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.storage.ProjectStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.flatMapFailure
import dev.forkhandles.result4k.onFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.describeTable
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema.Primary
import org.http4k.connect.amazon.dynamodb.mapper.minusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

fun ProjectStorage.Companion.dynamoDb(
    dynamoDb: DynamoDb, tableName: TableName, autoCreate: Boolean = false
) = DynamoProjectStorage(dynamoDb, tableName, autoCreate)

class DynamoProjectStorage internal constructor(
    dynamoDb: DynamoDb,
    tableName: TableName,
    private val autoCreate: Boolean = false
): ProjectStorage {

    private val table = dynamoDb.tableMapper(tableName, Primary<DynamoProject, ProjectName, Unit>(
        hashKeyAttribute = ProjectName.attribute,
        sortKeyAttribute = null,
        lens = togglesJson.autoDynamoLens<DynamoProject>()
    ))

    init {
        dynamoDb.describeTable(tableName)
            .flatMapFailure { if (autoCreate) table.createTable() else it.asFailure() }
            .onFailure { it.reason.throwIt() }
    }

    override fun list(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = table.primaryIndex().scanPage(
            ExclusiveStartKey = cursor?.let { Key(ProjectName.attribute of cursor) },
            Limit = pageSize
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(ProjectName.attribute)
        )
    }

    override fun get(projectName: ProjectName) = table[projectName]?.toModel()
    override fun plusAssign(project: Project) = table.save(project.toDynamo())
    override fun minusAssign(project: Project) = table.minusAssign(project.toDynamo())
}

@JsonSerializable
internal data class DynamoProject(
    val projectName: ProjectName,
    val createdOn: Instant
)

private fun DynamoProject.toModel() = Project(
    projectName = projectName,
    createdOn = createdOn
)

private fun Project.toDynamo() = DynamoProject(
    projectName = projectName,
    createdOn = createdOn
)