package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.storage.ProjectStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

internal fun dynamoProjectStorage(
    projects: DynamoDbTableMapper<DynamoProject, ProjectName, Unit>
) = object : ProjectStorage {

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

@JsonSerializable
internal data class DynamoProject(
    val projectName: ProjectName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val environments: List<EnvironmentName>
)

internal fun DynamoProject.toModel() = Project(
    projectName = projectName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    environments = environments
)

internal fun Project.toDynamo() = DynamoProject(
    projectName = projectName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    environments = environments
)