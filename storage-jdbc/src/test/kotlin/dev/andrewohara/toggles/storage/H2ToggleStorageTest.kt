package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ApiKeysStorageContract
import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.toggles.ToggleStorageContract
import org.h2.jdbcx.JdbcDataSource
import java.util.UUID

private fun create() = JdbcDataSource()
    .apply { setURL("jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1") }
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