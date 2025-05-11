package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.tenants.Tenant
import dev.andrewohara.toggles.tenants.TenantStorage
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

private const val LIST = """
    SELECT *
    FROM tenants
    WHERE tenant_id >= ?
    ORDER BY tenant_id ASC
    LIMIT ?
"""

private const val GET = """
    SELECT *
    FROM tenants
    WHERE tenant_id = ?
"""

private const val INSERT = """
    INSERT INTO tenants (tenant_id, created_on)
    VALUES (?, ?)
"""

private const val DELETE = """
    DELETE FROM tenants
    WHERE tenant_id = ?
"""

internal fun jdbcTenantStorage(dataSource: DataSource) = object: TenantStorage {

    override fun list(pageSize: Int) = Paginator<Tenant, TenantId> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST).use { stmt ->
                stmt.setString(1, cursor?.toString() ?: "")
                stmt.setInt(2, pageSize + 1)

                stmt.executeQuery().use { rs ->
                    rs.toSequence().map { it.toTenant() }.toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.tenantId
        )
    }

    override fun get(tenantId: TenantId) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, tenantId.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toTenant() else null
            }
        }
    }

    override fun plusAssign(tenant: Tenant) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(INSERT).use { stmt ->
                stmt.setString(1, tenant.tenantId.value)
                stmt.setTimestamp(2, Timestamp.from(tenant.createdOn))

                stmt.executeUpdate()
            }
        }
    }

    override fun minusAssign(tenant: Tenant) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, tenant.tenantId.value)

                stmt.executeUpdate()
            }
        }
    }
}

private fun ResultSet.toTenant() = Tenant(
    tenantId = TenantId.of(getString("tenant_id")),
    createdOn = getTimestamp("created_on").toInstant()
)