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
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens

internal fun dynamoProjectStorage(dynamoDb: DynamoDb, tableName: TableName, autoCreate: Boolean): ProjectStorage {
    val projects = dynamoDb.tableMapper(
        tableName, Primary<DynamoProject, ProjectName, Unit>(
            hashKeyAttribute = ProjectName.attribute,
            sortKeyAttribute = null,
            lens = togglesJson.autoDynamoLens()
        )
    )

    dynamoDb.describeTable(tableName)
        .flatMapFailure { if (autoCreate) projects.createTable() else it.asFailure() }
        .onFailure { it.reason.throwIt() }

    return object : ProjectStorage {
        override fun list(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
            val page = projects.primaryIndex().scanPage(
                ExclusiveStartKey = cursor?.let { Key(ProjectName.attribute of cursor) },
                Limit = pageSize
            )

            Page(
                items = page.items.map { it.toModel() },
                next = page.lastEvaluatedKey?.let(ProjectName.attribute)
            )
        }

        override fun get(projectName: ProjectName) = projects[projectName]?.toModel()

        override fun plusAssign(project: Project) {
            projects.save(project.toDynamo())
        }

        override fun minusAssign(projectName: ProjectName) = projects.delete(projectName)
    }
}