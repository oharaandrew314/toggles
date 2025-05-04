package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleEnvironment
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.utils.jdbc.getStringOrNull
import dev.andrewohara.utils.jdbc.toSequence
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.lens.BiDiMapping
import org.http4k.lens.StringBiDiMappings
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
        toggles.project_name = ?
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
        toggles.project_name = ?
        AND toggles.toggle_name = ?
"""

private const val INSERT_TOGGLE = """
    INSERT INTO toggles (project_name, toggle_name, unique_id, created_on, updated_on, variations, default_variation)
    VALUES (?, ?, ?, ?, ?, ?, ?)
"""

private const val INSERT_ENVIRONMENT = """
    INSERT INTO toggle_environments
    (project_name, toggle_name, environment, weights, overrides)
    VALUES (?, ?, ?, ?, ?)
"""

private const val DELETE_TOGGLE = "DELETE FROM toggles WHERE project_name = ? AND toggle_name = ?"

private const val LIST_PROJECTS = "SELECT * FROM projects WHERE project_name >= ? ORDER BY project_name ASC LIMIT ?"

private const val GET_PROJECT = "SELECT * FROM projects WHERE project_name = ?"

private const val INSERT_PROJECT = """
    INSERT INTO projects (project_name, created_on, updated_on, environments)
    VALUES (?, ?, ?, ?)
"""

private const val DELETE_PROJECT = "DELETE FROM projects WHERE project_name = ?"

fun ToggleStorage.Companion.jdbc(dataSource: DataSource) = JdbcToggleStorage(dataSource)

/**
 * Database agnostic JDBC storage.
 */
class JdbcToggleStorage internal constructor(private val dataSource: DataSource): ToggleStorage {

    override fun listProjects(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(LIST_PROJECTS).use { stmt ->
                stmt.setString(1, cursor?.value ?: "")
                stmt.setInt(2, pageSize + 1)

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

    override fun getProject(projectName: ProjectName) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET_PROJECT).use { stmt ->
            stmt.setString(1, projectName.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toProject() else null
            }
        }
    }

    override fun upsertProject(project: Project) {
        dataSource.transaction {
            prepareStatement(DELETE_PROJECT).use { stmt ->
                stmt.setString(1, project.projectName.value)

                stmt.executeUpdate()
            }

            prepareStatement(INSERT_PROJECT).use { stmt ->
                stmt.setString(1, project.projectName.value)
                stmt.setTimestamp(2, Timestamp.from(project.createdOn))
                stmt.setTimestamp(3, Timestamp.from(project.updatedOn))
                stmt.setString(4, environmentsMapping(project.environments))

                stmt.executeUpdate()
            }
        }
    }

    override fun deleteProject(projectName: ProjectName) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE_PROJECT).use { stmt ->
                stmt.setString(1, projectName.value)

                stmt.executeUpdate()
            }
        }
    }

    override fun listToggles(
        projectName: ProjectName,
        pageSize: Int
    ) = Paginator<Toggle, ToggleName> { cursor ->
        val page = dataSource.connection.use { conn ->
            conn.prepareStatement(
                LIST_TOGGLES,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            ).use { stmt ->
                stmt.setString(1, projectName.value)
                stmt.setString(2, cursor?.value ?: "")

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

    override fun getToggle(projectName: ProjectName, toggleName: ToggleName) = dataSource.connection.use { conn ->
        conn.prepareStatement(
            GET_TOGGLE,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        ).use { stmt ->
            stmt.setString(1, projectName.value)
            stmt.setString(2, toggleName.value)

            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toToggle() else null
            }
        }
    }

    override fun upsertToggle(toggle: Toggle) {
        dataSource.transaction {
            prepareStatement(DELETE_TOGGLE).use { stmt ->
                stmt.setString(1, toggle.projectName.value)
                stmt.setString(2, toggle.toggleName.value)

                stmt.executeUpdate()
            }

            prepareStatement(INSERT_TOGGLE).use { stmt ->
                stmt.setString(1, toggle.projectName.value)
                stmt.setString(2, toggle.toggleName.value)
                stmt.setString(3, toggle.uniqueId.value)
                stmt.setTimestamp(4, Timestamp.from(toggle.createdOn))
                stmt.setTimestamp(5, Timestamp.from(toggle.updatedOn))
                stmt.setString(6, variationsMapping(toggle.variations))
                stmt.setString(7, toggle.defaultVariation.value)

                stmt.executeUpdate()
            }

            for ((envName, env) in toggle.environments) {
                prepareStatement(INSERT_ENVIRONMENT).use { stmt ->
                    stmt.setString(1, toggle.projectName.value)
                    stmt.setString(2, toggle.toggleName.value)
                    stmt.setString(3, envName.value)
                    stmt.setString(4, weightsMapping(env.weights))
                    stmt.setString(5, overridesMapping(env.overrides))

                    stmt.executeUpdate()
                }
            }
        }
    }

    override fun deleteToggle(projectName: ProjectName, toggleName: ToggleName) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE_TOGGLE).use { stmt ->
                stmt.setString(1, projectName.value)
                stmt.setString(2, toggleName.value)

                stmt.execute()
            }
        }
    }
}

private fun ResultSet.toToggle(): Toggle {
    val toggle = Toggle(
        projectName = ProjectName.parse(getString("project_name")),
        toggleName = ToggleName.parse(getString("toggle_name")),
        uniqueId = UniqueId.parse(getString("unique_id")),
        createdOn = getTimestamp("created_on").toInstant(),
        updatedOn = getTimestamp("updated_on").toInstant(),
        variations = variationsMapping(getString("variations")),
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
                weights = weightsMapping(getString("weights")),
                overrides = overridesMapping(getString("overrides")),
            )
        } while(next())
    }

    // If we processed at least one environment, move back so the subsequent next() call will move to the next toggle
    if (environments.isNotEmpty()) {
        previous()
    }

    return toggle.copy(environments = environments)
}

private fun ResultSet.toProject() = Project(
    projectName = ProjectName.parse(getString("project_name")),
    createdOn = getTimestamp("created_on").toInstant(),
    updatedOn = getTimestamp("updated_on").toInstant(),
    environments = environmentsMapping(getString("environments"))
)

private val environmentsMapping = StringBiDiMappings.csv(mapElement = BiDiMapping(EnvironmentName::of, EnvironmentName::show))
private val variationsMapping = StringBiDiMappings.csv(mapElement = BiDiMapping(VariationName::of, VariationName::show))
private val weightsMapping = keyValuePairsMapping(VariationName.Companion, Weight.Companion)
private val overridesMapping = keyValuePairsMapping(SubjectId.Companion, VariationName.Companion)