package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

private const val LIST = "SELECT * FROM projects WHERE project_name >= ? ORDER BY project_name ASC LIMIT ?"
private const val GET = "SELECT * FROM projects WHERE project_name = ?"
private const val INSERT = "INSERT INTO projects (project_name, created_on) VALUES (?, ?)"
private const val DELETE = "DELETE FROM projects WHERE project_name = ?"

fun ProjectStorage.Companion.jdbc(dataSource: DataSource) = JdbcProjectStorage(dataSource)

class JdbcProjectStorage internal constructor(private val dataSource: DataSource): ProjectStorage {

    override fun list(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST).use { stmt ->
                stmt.setString(1, cursor?.value ?: "")
                stmt.setInt(2, pageSize + 1)

                stmt.executeQuery().use { rs ->
                    rs.toSequence().map { it.toModel() }.toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.projectName
        )
    }

    override fun get(projectName: ProjectName) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, projectName.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toModel() else null
            }
        }
    }

    override fun plusAssign(project: Project) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(INSERT).use { stmt ->
                stmt.setString(1, project.projectName.value)
                stmt.setTimestamp(2, Timestamp.from(project.createdOn))

                stmt.execute()
            }
        }
    }

    override fun minusAssign(project: Project) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, project.projectName.value)

                stmt.execute()
            }
        }
    }
}

private fun ResultSet.toModel() = Project(
    projectName = ProjectName.parse(getString("project_name")),
    createdOn = getTimestamp("created_on").toInstant()
)