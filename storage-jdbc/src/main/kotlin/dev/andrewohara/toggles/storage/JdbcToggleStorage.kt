package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource

private const val LIST = """
    SELECT * FROM toggles
    WHERE project_name = ? AND toggle_name >= ?
    ORDER BY project_name ASC, toggle_name ASC
    LIMIT ?
"""
private const val GET = "SELECT * FROM toggles WHERE project_name = ? AND toggle_name = ?"
private const val INSERT = """
    INSERT INTO toggles (project_name, toggle_name, created_on, updated_on, variations, overrides, default_variation)
    VALUES (?, ?, ?, ?, ?, ?, ?)
"""
private const val UPDATE = """
    UPDATE toggles
    SET updated_on = ?, variations = ?, overrides = ?, default_variation = ?
    WHERE project_name = ? AND toggle_name = ?
"""
private const val DELETE = "DELETE FROM toggles WHERE project_name = ? AND toggle_name = ?"

fun ToggleStorage.Companion.jdbc(dataSource: DataSource) = JdbcToggleStorage(dataSource)

/**
 * Database agnostic JDBC storage.
 */
class JdbcToggleStorage internal constructor(private val dataSource: DataSource): ToggleStorage {

    override fun list(
        projectName: ProjectName,
        pageSize: Int
    ) = Paginator<Toggle, ToggleName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST).use { stmt ->
                stmt.setString(1, projectName.value)
                stmt.setString(2, cursor?.value ?: "")
                stmt.setInt(3, pageSize + 1)

                stmt.executeQuery().use { rs ->
                    rs.toSequence().map { it.toToggle() }.toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.toggleName
        )
    }

    override fun get(projectName: ProjectName, toggleName: ToggleName) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, projectName.value)
            stmt.setString(2, toggleName.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toToggle() else null
            }
        }
    }

    override fun plusAssign(toggle: Toggle) {
        dataSource.connection.use { conn ->
            val updated = conn.prepareStatement(UPDATE).use { stmt ->
                stmt.setTimestamp(1, Timestamp.from(toggle.updatedOn))
                stmt.setString(2, toggle.variations.serialize())
                stmt.setString(3, toggle.overrides.serialize())
                stmt.setString(4, toggle.defaultVariation.value)
                stmt.setString(5, toggle.projectName.value)
                stmt.setString(6, toggle.toggleName.value)

                stmt.executeUpdate() == 1
            }

            if (!updated) {
                conn.prepareStatement(INSERT).use { stmt ->
                    stmt.setString(1, toggle.projectName.value)
                    stmt.setString(2, toggle.toggleName.value)
                    stmt.setTimestamp(3, Timestamp.from(toggle.createdOn))
                    stmt.setTimestamp(4, Timestamp.from(toggle.updatedOn))
                    stmt.setString(5, toggle.variations.serialize())
                    stmt.setString(6, toggle.overrides.serialize())
                    stmt.setString(7, toggle.defaultVariation.value)

                    stmt.execute()
                }
            }
        }
    }

    override fun minusAssign(toggle: Toggle) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE).use { stmt ->
                stmt.setString(1, toggle.projectName.value)
                stmt.setString(2, toggle.toggleName.value)

                stmt.execute()
            }
        }
    }
}

private fun ResultSet.toToggle() = Toggle(
    projectName = ProjectName.parse(getString("project_name")),
    toggleName = ToggleName.parse(getString("toggle_name")),
    createdOn = getTimestamp("created_on").toInstant(),
    updatedOn = getTimestamp("updated_on").toInstant(),
    variations = getString("variations").deserialize(VariationName, Weight),
    overrides = getString("overrides").deserialize(SubjectId, VariationName),
    defaultVariation = VariationName.parse(getString("default_variation"))
)

private fun <K: Value<*>, V: Value<*>> String.deserialize(
    keyFactory: ValueFactory<K, *>,
    valueFactory: ValueFactory<V, *>
) = split(",").associate {
    val (key, value) = it.split("=")
    keyFactory.parse(key) to valueFactory.parse(value)
}

private fun Map<out Value<*>, Value<*>>.serialize() =
    entries.joinToString(",") { (key, value) -> "$key=$value" }