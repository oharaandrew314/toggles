package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.projects.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.projects.ProjectStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.model.Key
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

internal fun dynamoProjectStorage(
    projects: DynamoDbTableMapper<DynamoProject, TenantId, ProjectName>
) = object : ProjectStorage {

    override fun list(tenantId: TenantId, pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = projects.primaryIndex().scanPage(
            ExclusiveStartKey = cursor
                ?.let { Key(TenantId.attribute of tenantId, ProjectName.attribute of cursor) },
            Limit = pageSize
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(ProjectName.attribute)
        )
    }

    override fun get(tenantId: TenantId, projectName: ProjectName) = projects[tenantId, projectName]?.toModel()

    override fun plusAssign(project: Project) {
        projects.save(project.toDynamo())
    }

    override fun minusAssign(project: Project) = projects.delete(project.tenantId, project.projectName)
}

@JsonSerializable
internal data class DynamoProject(
    val tenantId: TenantId,
    val projectName: ProjectName,
    val createdOn: Instant,
    val updatedOn: Instant,
    val environments: List<EnvironmentName>
)

private fun DynamoProject.toModel() = Project(
    tenantId = tenantId,
    projectName = projectName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    environments = environments
)

private fun Project.toDynamo() = DynamoProject(
    tenantId = tenantId,
    projectName = projectName,
    createdOn = createdOn,
    updatedOn = updatedOn,
    environments = environments
)