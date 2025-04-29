package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ToggleStorageContract
import org.h2.jdbcx.JdbcDataSource
import java.util.UUID

class H2JdbcToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = JdbcDataSource()
        .apply { setURL("jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1") }
        .also { it.executeResource("init.sql") }
        .let { ToggleStorage.jdbc(it) }
}