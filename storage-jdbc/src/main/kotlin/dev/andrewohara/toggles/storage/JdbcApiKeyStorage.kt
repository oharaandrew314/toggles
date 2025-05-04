package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.TokenMd5
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import javax.sql.DataSource

private const val LIST = """
    SELECT project_name, environment_name, created_on
    FROM api_keys 
    WHERE project_name = ? AND environment_name >= ?
    ORDER BY environment_name ASC
    LIMIT ?
"""

private const val GET = """
    SELECT project_name, environment_name, created_on
    FROM api_keys 
    WHERE project_name = ? AND environment_name = ?
"""

private const val INSERT = """
    INSERT INTO api_keys (project_name, environment_name, created_on, token_md5_hex)
    VALUES (?, ?, ?, ?)
"""

private const val DELETE = """
    DELETE FROM api_keys 
    WHERE project_name = ? AND environment_name = ?
"""

private const val LOOKUP = """
    SELECT project_name, environment_name, created_on
    FROM api_keys 
    WHERE token_md5_hex = ?
    LIMIT 1
"""

fun jdbcApiKeyStorage(dataSource: DataSource) = object: ApiKeyStorage {
    override fun list(
        projectName: ProjectName,
        pageSize: Int,
    ) = Paginator<ApiKeyMeta, EnvironmentName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST).use { stmt ->
                stmt.setString(1, projectName.value)
                stmt.setString(2, cursor?.value ?: "")
                stmt.setInt(3, pageSize + 1)

                stmt.executeQuery().use { rs ->
                    rs.toSequence().map { it.toModel() }.toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.environment
        )
    }

    override fun get(
        projectName: ProjectName,
        environment: EnvironmentName,
    ) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, projectName.value)
            stmt.setString(2, environment.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toModel() else null
            }
        }
    }

    override fun set(meta: ApiKeyMeta, tokenMd5: TokenMd5) {
        dataSource.transaction {
            prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, meta.projectName.value)
                stmt.setString(2, meta.environment.value)

                stmt.executeUpdate()
            }

            prepareStatement(INSERT).use { stmt ->
                stmt.setString(1, meta.projectName.value)
                stmt.setString(2, meta.environment.value)
                stmt.setTimestamp(3, java.sql.Timestamp.from(meta.createdOn))
                stmt.setString(4, tokenMd5.toString())

                stmt.executeUpdate()
            }
        }
    }

    override fun minusAssign(meta: ApiKeyMeta) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, meta.projectName.value)
                stmt.setString(2, meta.environment.value)

                stmt.executeUpdate()
            }
        }
    }

    override fun get(tokenMd5: TokenMd5) = dataSource.connection.use { conn ->
        conn.prepareStatement(LOOKUP).use { stmt ->
            stmt.setString(1, tokenMd5.toString())

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toModel() else null
            }
        }
    }
}

private fun ResultSet.toModel() = ApiKeyMeta(
    projectName = ProjectName.of(getString("project_name")),
    environment = EnvironmentName.of(getString("environment_name")),
    createdOn = getTimestamp("created_on").toInstant()
)