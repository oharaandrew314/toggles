package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.toggles.Toggle
import dev.andrewohara.toggles.toggles.ToggleEnvironment
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.toggles.ToggleStorage
import dev.andrewohara.utils.jdbc.getStringOrNull
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.sql.ResultSet
import java.sql.Timestamp
import javax.sql.DataSource
import kotlin.collections.component1
import kotlin.collections.component2

private const val LIST_TOGGLES = """
    SELECT toggles.*, envs.environment, envs.weights, envs.overrides
    FROM toggles
    LEFT JOIN toggle_environments envs
        ON toggles.project_name = envs.project_name
        AND toggles.toggle_name = envs.toggle_name
    WHERE
        toggles.tenant_id = ?
        AND toggles.project_name = ?
        AND toggles.toggle_name >= ?
    ORDER BY project_name ASC, toggle_name ASC
"""
private const val GET_TOGGLE = """
    SELECT toggles.*, envs.environment, envs.weights, envs.overrides
    FROM toggles
    LEFT JOIN toggle_environments envs
        ON toggles.project_name = envs.project_name
        AND toggles.toggle_name = envs.toggle_name
    WHERE
        toggles.tenant_id = ?
        AND toggles.project_name = ?
        AND toggles.toggle_name = ?
"""

private const val INSERT_TOGGLE = """
    INSERT INTO toggles (tenant_id, project_name, toggle_name, created_on, updated_on, variations, default_variation)
    VALUES (?, ?, ?, ?, ?, ?, ?)
"""

private const val INSERT_ENVIRONMENT = """
    INSERT INTO toggle_environments
    (tenant_id, project_name, toggle_name, environment, weights, overrides)
    VALUES (?, ?, ?, ?, ?, ?)
"""

private const val DELETE_TOGGLE = """
    DELETE FROM toggles
    WHERE tenant_id = ? AND project_name = ? AND toggle_name = ?
"""

internal fun jdbcToggleStorage(dataSource: DataSource) = object: ToggleStorage {
    override fun list(
        tenantId: TenantId,
        projectName: ProjectName,
        pageSize: Int
    ) = Paginator<Toggle, ToggleName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(
                LIST_TOGGLES,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            ).use { stmt ->
                stmt.setString(1, tenantId.value)
                stmt.setString(2, projectName.value)
                stmt.setString(3, cursor?.value ?: "")

                stmt.executeQuery().use { rs ->
                    rs.toSequence()
                        .map { it.toToggle() }
                        .take(pageSize + 1)
                        .toList()
                }
            }
        }

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.toggleName
        )
    }

    override fun get(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName) = dataSource.connection.use { conn ->
        conn.prepareStatement(
            GET_TOGGLE,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        ).use { stmt ->
            stmt.setString(1, tenantId.value)
            stmt.setString(2, projectName.value)
            stmt.setString(3, toggleName.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toToggle() else null
            }
        }
    }

    override fun plusAssign(toggle: Toggle) {
        dataSource.transaction {
            prepareStatement(DELETE_TOGGLE).use { stmt ->
                stmt.setString(1, toggle.tenantId.value)
                stmt.setString(2, toggle.projectName.value)
                stmt.setString(3, toggle.toggleName.value)

                stmt.executeUpdate()
            }

            prepareStatement(INSERT_TOGGLE).use { stmt ->
                stmt.setString(1, toggle.tenantId.value)
                stmt.setString(2, toggle.projectName.value)
                stmt.setString(3, toggle.toggleName.value)
                stmt.setTimestamp(4, Timestamp.from(toggle.createdOn))
                stmt.setTimestamp(5, Timestamp.from(toggle.updatedOn))
                stmt.setString(6, toggle.variations.toCsv())
                stmt.setString(7, toggle.defaultVariation.value)

                stmt.executeUpdate()
            }

            for ((envName, env) in toggle.environments) {
                prepareStatement(INSERT_ENVIRONMENT).use { stmt ->
                    stmt.setString(1, toggle.tenantId.value)
                    stmt.setString(2, toggle.projectName.value)
                    stmt.setString(3, toggle.toggleName.value)
                    stmt.setString(4, envName.value)
                    stmt.setString(5, env.weights.toKeyValuePairs())
                    stmt.setString(6, env.overrides.toKeyValuePairs())

                    stmt.executeUpdate()
                }
            }
        }
    }

    override fun minusAssign(toggle: Toggle) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE_TOGGLE).use { stmt ->
                stmt.setString(1, toggle.tenantId.value)
                stmt.setString(2, toggle.projectName.value)
                stmt.setString(3, toggle.toggleName.value)

                stmt.execute()
            }
        }
    }
}

private fun ResultSet.toToggle(): Toggle {
    val toggle = Toggle(
        tenantId = TenantId.parse(getString("tenant_id")),
        projectName = ProjectName.parse(getString("project_name")),
        toggleName = ToggleName.parse(getString("toggle_name")),
        createdOn = getTimestamp("created_on").toInstant(),
        updatedOn = getTimestamp("updated_on").toInstant(),
        variations = getString("variations").parseCsv(VariationName),
        defaultVariation = VariationName.parse(getString("default_variation")),
        environments = emptyMap()
    )

    // Each environment takes up a single row
    val environments = buildMap {
        do {
            val envToggleName = getString("toggle_name")
            val envName = getStringOrNull("environment")?.let(EnvironmentName::of)

            if (envName == null || envToggleName != toggle.toggleName.value) break

            this[envName] = ToggleEnvironment(
                weights = getString("weights").parseKeyValuePairs(VariationName, Weight),
                overrides = getString("overrides").parseKeyValuePairs(SubjectId, VariationName),
            )
        } while (next())
    }

    // If we processed at least one environment, move back so the subsequent next() call will move to the next toggle
    if (environments.isNotEmpty()) {
        previous()
    }

    return toggle.copy(environments = environments)
}