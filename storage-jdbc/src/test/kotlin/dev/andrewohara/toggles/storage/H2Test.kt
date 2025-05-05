package dev.andrewohara.toggles.storage

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.andrewohara.toggles.ApiKeysStorageContract
import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.toggles.ProjectsHttpContract
import dev.andrewohara.toggles.Storage
import dev.andrewohara.toggles.ToggleStorageContract
import dev.andrewohara.toggles.TogglesHttpContract
import java.util.UUID

private fun create() = HikariConfig()
    .also { it.jdbcUrl = "jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1" }
    .let(::HikariDataSource)
    .let { Storage.jdbc(it) }

class H2ToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = create()
}

class H2ProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = create()
}

class H2ApiKeyStorageTest: ApiKeysStorageContract() {
    override fun createStorage() = create()
}

class H2TogglesHttpTest: TogglesHttpContract() {
    override fun createStorage() = create()
}

class H2ProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = create()
}