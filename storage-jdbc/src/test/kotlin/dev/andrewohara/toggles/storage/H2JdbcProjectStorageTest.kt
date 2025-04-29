package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract
import org.h2.jdbcx.JdbcDataSource
import java.util.UUID

class H2JdbcProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = JdbcDataSource()
        .apply { setURL("jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1") }
        .also { it.executeResource("init.sql") }
        .let { ProjectStorage.jdbc(it) }
}