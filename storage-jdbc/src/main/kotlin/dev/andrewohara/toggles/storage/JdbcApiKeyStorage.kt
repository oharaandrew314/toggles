package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.ApiKeyHash
import dev.andrewohara.toggles.apikeys.ApiKeyStorage
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import javax.sql.DataSource

private const val LIST = """
    SELECT tenant_id, project_name, environment_name, created_on
    FROM api_keys 
    WHERE project_name = ? AND environment_name >= ?
    ORDER BY environment_name ASC
    LIMIT ?
"""

private const val GET = """
    SELECT tenant_id, project_name, environment_name, created_on
    FROM api_keys 
    WHERE project_name = ? AND environment_name = ?
"""

private const val INSERT = """
    INSERT INTO api_keys (tenant_id, project_name, environment_name, created_on, token_sha256_hex)
    VALUES (?, ?, ?, ?, ?)
"""

private const val DELETE = """
    DELETE FROM api_keys 
    WHERE tenant_id = ? AND project_name = ? AND environment_name = ?
"""

private const val LOOKUP = """
    SELECT tenant_id, project_name, environment_name, created_on
    FROM api_keys 
    WHERE token_sha256_hex = ?
    LIMIT 1
"""

fun jdbcApiKeyStorage(dataSource: DataSource) = object: ApiKeyStorage {
    override fun list(
        tenantId: TenantId,
        projectName: ProjectName,
        pageSize: Int,
    ) = Paginator<ApiKeyMeta, EnvironmentName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST).use { stmt ->
                stmt.setString(1, tenantId.value)
                stmt.setString(2, projectName.value)
                stmt.setString(3, cursor?.value ?: "")
                stmt.setInt(4, pageSize + 1)

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
        tenantId: TenantId,
        projectName: ProjectName,
        environment: EnvironmentName,
    ) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, tenantId.value)
            stmt.setString(2, projectName.value)
            stmt.setString(3, environment.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toModel() else null
            }
        }
    }

    override fun set(meta: ApiKeyMeta, apiKeyHash: ApiKeyHash) {
        dataSource.transaction {
            prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, meta.tenantId.value)
                stmt.setString(2, meta.projectName.value)
                stmt.setString(3, meta.environment.value)

                stmt.executeUpdate()
            }

            prepareStatement(INSERT).use { stmt ->
                stmt.setString(1, meta.tenantId.value)
                stmt.setString(2, meta.projectName.value)
                stmt.setString(3, meta.environment.value)
                stmt.setTimestamp(4, java.sql.Timestamp.from(meta.createdOn))
                stmt.setString(5, apiKeyHash.toString())

                stmt.executeUpdate()
            }
        }
    }

    override fun minusAssign(meta: ApiKeyMeta) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, meta.tenantId.value)
                stmt.setString(2, meta.projectName.value)
                stmt.setString(3, meta.environment.value)

                stmt.executeUpdate()
            }
        }
    }

    override fun get(apiKeyHash: ApiKeyHash) = dataSource.connection.use { conn ->
        conn.prepareStatement(LOOKUP).use { stmt ->
            stmt.setString(1, apiKeyHash.toString())

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toModel() else null
            }
        }
    }
}

private fun ResultSet.toModel() = ApiKeyMeta(
    tenantId = TenantId.parse(getString("tenant_id")),
    projectName = ProjectName.of(getString("project_name")),
    environment = EnvironmentName.of(getString("environment_name")),
    createdOn = getTimestamp("created_on").toInstant()
)