package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.projects.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.projects.ProjectStorage
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

private const val LIST_PROJECTS = """
    SELECT *
    FROM projects
    WHERE tenant_id = ? AND project_name >= ?
    ORDER BY project_name ASC LIMIT ?
"""

private const val GET_PROJECT = """
    SELECT *
    FROM projects
    WHERE tenant_id = ? AND project_name = ?
"""

private const val INSERT_PROJECT = """
    INSERT INTO projects (tenant_id, project_name, created_on, updated_on, environments)
    VALUES (?, ?, ?, ?, ?)
"""

private const val DELETE_PROJECT = "DELETE FROM projects WHERE tenant_id = ? AND project_name = ?"


internal fun jdbcProjectStorage(dataSource: DataSource) = object: ProjectStorage {

    override fun list(tenantId: TenantId, pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST_PROJECTS).use { stmt ->
                stmt.setString(1, tenantId.value)
                stmt.setString(2, cursor?.value ?: "")
                stmt.setInt(3, pageSize + 1)

                stmt.executeQuery().use { rs ->
                    rs.toSequence().map { it.toProject() }.toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.projectName
        )
    }

    override fun get(tenantId: TenantId, projectName: ProjectName) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET_PROJECT).use { stmt ->
            stmt.setString(1, tenantId.value)
            stmt.setString(2, projectName.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toProject() else null
            }
        }
    }

    override fun plusAssign(project: Project) {
        dataSource.transaction {
            prepareStatement(DELETE_PROJECT).use { stmt ->
                stmt.setString(1, project.tenantId.value)
                stmt.setString(2, project.projectName.value)

                stmt.executeUpdate()
            }

            prepareStatement(INSERT_PROJECT).use { stmt ->
                stmt.setString(1, project.tenantId.value)
                stmt.setString(2, project.projectName.value)
                stmt.setTimestamp(3, Timestamp.from(project.createdOn))
                stmt.setTimestamp(4, Timestamp.from(project.updatedOn))
                stmt.setString(5, project.environments.toCsv())

                stmt.executeUpdate()
            }
        }
    }

    override fun minusAssign(project: Project) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE_PROJECT).use { stmt ->
                stmt.setString(1, project.tenantId.value)
                stmt.setString(2, project.projectName.value)

                stmt.executeUpdate()
            }
        }
    }
}

private fun ResultSet.toProject() = Project(
    tenantId = TenantId.parse(getString("tenant_id")),
    projectName = ProjectName.parse(getString("project_name")),
    createdOn = getTimestamp("created_on").toInstant(),
    updatedOn = getTimestamp("updated_on").toInstant(),
    environments = getString("environments").parseCsv(EnvironmentName)
)