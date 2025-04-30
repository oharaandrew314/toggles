package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.Project
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


fun ToggleStorage.Companion.dynamoDb(
    dynamoDb: DynamoDb,
    projectsTableName: TableName,
    togglesTableName: TableName,
    autoCreate: Boolean = false
) = DynamoToggleStorage(
    dynamoDb = dynamoDb,
    projectsTableName = projectsTableName,
    togglesTableName = togglesTableName,
    autoCreate = autoCreate
)

class DynamoToggleStorage internal constructor(
    dynamoDb: DynamoDb,
    projectsTableName: TableName,
    togglesTableName: TableName,
    private val autoCreate: Boolean = false
): ToggleStorage {

    private val projects = dynamoDb.tableMapper(projectsTableName, Primary<DynamoProject, ProjectName, Unit>(
        hashKeyAttribute = ProjectName.attribute,
        sortKeyAttribute = null,
        lens = togglesJson.autoDynamoLens()
    ))

    private val toggles = dynamoDb.tableMapper(togglesTableName, Primary<DynamoToggle, ProjectName, ToggleName>(
        hashKeyAttribute = ProjectName.attribute,
        sortKeyAttribute = ToggleName.attribute,
        lens = togglesJson.autoDynamoLens()
    ))

    init {
        dynamoDb.describeTable(projectsTableName)
            .flatMapFailure { if (autoCreate) projects.createTable() else it.asFailure() }
            .onFailure { it.reason.throwIt() }

        dynamoDb.describeTable(togglesTableName)
            .flatMapFailure { if (autoCreate) toggles.createTable() else it.asFailure() }
            .onFailure { it.reason.throwIt() }
    }

    override fun listToggles(
        projectName: ProjectName,
        pageSize: Int
    ) =  Paginator<Toggle, ToggleName> { cursor ->
        val page = toggles.primaryIndex().queryPage(
            HashKey = projectName,
            Limit = pageSize,
            ExclusiveStartKey = cursor?.let { Key(ProjectName.attribute of projectName, ToggleName.attribute of cursor) }
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(ToggleName.attribute)
        )
    }

    override fun getToggle(projectName: ProjectName, toggleName: ToggleName) = toggles[projectName, toggleName]?.toModel()

    override fun upsertToggle(toggle: Toggle) = toggles.plusAssign(toggle.toDynamo())

    override fun deleteToggle(projectName: ProjectName, toggleName: ToggleName) = toggles.delete(projectName, toggleName)

    override fun listProjects(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = projects.primaryIndex().scanPage(
            ExclusiveStartKey = cursor?.let { Key(ProjectName.attribute of cursor) },
            Limit = pageSize
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(ProjectName.attribute)
        )
    }

    override fun getProject(projectName: ProjectName) = projects[projectName]?.toModel()

    override fun upsertProject(project: Project) {
        if (getProject(project.projectName) != null) return
        projects.save(project.toDynamo())
    }
    override fun deleteProject(projectName: ProjectName) = projects.delete(projectName)
}



