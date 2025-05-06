package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UserId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.UserRole
import dev.andrewohara.toggles.users.UserStorage
import dev.andrewohara.toggles.users.userRefMapping
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

private const val LIST_ALL = """
    SELECT *
    FROM users
    WHERE tenant_id > ? AND user_id > ?
    ORDER BY tenant_id ASC, user_id ASC
    LIMIT ?
"""

private const val LIST_BY_TENANT = """
    SELECT *
    FROM users
    WHERE tenant_id = ? AND user_id > ?
    ORDER BY user_id ASC
    LIMIT ?
"""

private const val GET = """
    SELECT *
    FROM users
    WHERE tenant_id = ? AND user_id = ?
"""

private const val GET_BY_EMAIL = """
    SELECT *
    FROM users
    WHERE email_address = ?
"""

private const val INSERT = """
    INSERT INTO users (tenant_id, user_id, email_address, created_on, role)
    VALUES (?, ?, ?, ?, ?)
"""

private const val DELETE = """
    DELETE FROM users
    WHERE tenant_id = ? AND user_id = ?
"""

internal fun jdbcUserStorage(dataSource: DataSource) = object: UserStorage {

    override fun list(pageSize: Int) = Paginator<User, String> { cursor ->
        val (tenantId, userId) = if (cursor == null) null to null else userRefMapping(cursor)

        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST_ALL).use { stmt ->
                stmt.setString(1, tenantId?.value ?: "")
                stmt.setString(2, userId?.value ?: "")
                stmt.setInt(3, pageSize + 1)

                stmt.executeQuery().use { rs ->
                    rs.toSequence().map { it.toUser() }.toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()
                ?.let { userRefMapping(it.tenantId to it.userId) }
        )
    }

    override fun list(tenantId: TenantId, pageSize: Int) = Paginator<User, UserId> { cursor ->
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
            next = page.drop(pageSize).firstOrNull()?.userId
        )
    }

    override fun get(tenantId: TenantId, userId: UserId) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, tenantId.value)
            stmt.setString(2, userId.value)

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
                stmt.setString(2, user.userId.value)
                stmt.setString(3, user.emailAddress.value)
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
                stmt.setString(2, user.userId.value)

                stmt.executeUpdate()
            }
        }
    }
}

private fun ResultSet.toUser() = User(
    tenantId = TenantId.parse(getString("tenant_id")),
    userId = UserId.parse(getString("user_id")),
    emailAddress = EmailAddress.parse(getString("email_address")),
    createdOn = getTimestamp("created_on").toInstant(),
    role = UserRole.valueOf(getString("role"))
)