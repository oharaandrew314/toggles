package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.storage.ProjectStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema.Primary
import org.http4k.connect.amazon.dynamodb.mapper.minusAssign
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

fun ProjectStorage.Companion.dynamoDb(
    table: DynamoDbTableMapper<DynamoProject, ProjectName, Unit>
) = DynamoProjectStorage(table)

fun ProjectStorage.Companion.dynamoDb(
    dynamoDb: DynamoDb, tableName: TableName
) = DynamoProjectStorage(dynamoDb.tableMapper(tableName, DynamoProjectStorage.primaryIndex))

class DynamoProjectStorage internal constructor(
    private val table: DynamoDbTableMapper<DynamoProject, ProjectName, Unit>
): ProjectStorage {
    companion object {
        val primaryIndex = Primary<DynamoProject, ProjectName, Unit>(
            hashKeyAttribute = ProjectName.attribute,
            sortKeyAttribute = null,
            lens = togglesJson.autoDynamoLens()
        )
    }

    override fun list(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = table.index(primaryIndex).scanPage(
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
data class DynamoProject(
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