package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.UserRole
import dev.andrewohara.toggles.users.UserStorage
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

private const val LIST_BY_TENANT = """
    SELECT *
    FROM users
    WHERE tenant_id = ? AND unique_id >= ?
    ORDER BY unique_id ASC
    LIMIT ?
"""

private const val GET = """
    SELECT *
    FROM users
    WHERE tenant_id = ? AND unique_id = ?
"""

private const val GET_BY_EMAIL = """
    SELECT *
    FROM users
    WHERE email_address = ?
"""

private const val INSERT = """
    INSERT INTO users (tenant_id, email_address, unique_id, created_on, role)
    VALUES (?, ?, ?, ?, ?)
"""

private const val DELETE = """
    DELETE FROM users
    WHERE tenant_id = ? AND unique_id = ?
"""

internal fun jdbcUserStorage(dataSource: DataSource) = object: UserStorage {

    override fun list(tenantId: TenantId, pageSize: Int) = Paginator<User, UniqueId> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST_BY_TENANT).use { stmt ->
                stmt.setString(1, tenantId.value)
                stmt.setString(2, cursor?.value ?: "")
                stmt.setInt(3, pageSize + 1)

                stmt.executeQuery().use { rs ->
                    rs.toSequence().map { it.toUser() }.toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.uniqueId
        )
    }

    override fun get(tenantId: TenantId, uniqueId: UniqueId) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, tenantId.value)
            stmt.setString(2, uniqueId.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toUser() else null
            }
        }
    }

    override fun get(emailAddress: EmailAddress) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET_BY_EMAIL).use { stmt ->
            stmt.setString(1, emailAddress.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toUser() else null
            }
        }
    }

    override fun plusAssign(user: User) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(INSERT).use { stmt ->
                stmt.setString(1, user.tenantId.value)
                stmt.setString(2, user.emailAddress.value)
                stmt.setString(3, user.uniqueId.value)
                stmt.setTimestamp(4, Timestamp.from(user.createdOn))
                stmt.setString(5, user.role.toString())

                stmt.executeUpdate()
            }
        }
    }

    override fun minusAssign(user: User) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, user.tenantId.value)
                stmt.setString(2, user.uniqueId.value)

                stmt.executeUpdate()
            }
        }
    }
}

private fun ResultSet.toUser() = User(
    tenantId = TenantId.parse(getString("tenant_id")),
    uniqueId = UniqueId.parse(getString("unique_id")),
    emailAddress = EmailAddress.parse(getString("email_address")),
    createdOn = getTimestamp("created_on").toInstant(),
    role = UserRole.valueOf(getString("role"))
)