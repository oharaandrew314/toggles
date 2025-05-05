package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.Storage
import org.flywaydb.core.Flyway
import javax.sql.DataSource


fun Storage.Companion.jdbc(
    dataSource: DataSource,
    autoMigrate: Boolean = true
): Storage {
    if (autoMigrate) {
        Flyway
            .configure()
            .dataSource(dataSource)
            .load()
            .migrate()
    }

    return Storage(
        projects = jdbcProjectStorage(dataSource),
        toggles = jdbcToggleStorage(dataSource),
        apiKeys = jdbcApiKeyStorage(dataSource),
        users = TODO(),
        tenants = TODO(),
    )
}