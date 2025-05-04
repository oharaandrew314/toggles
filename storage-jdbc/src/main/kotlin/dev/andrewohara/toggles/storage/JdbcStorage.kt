package dev.andrewohara.toggles.storage

import javax.sql.DataSource

fun Storage.Companion.jdbc(
    dataSource: DataSource,
    autoMigrate: Boolean = true // TODO
) = Storage(
    projects = jdbcProjectStorage(dataSource),
    toggles = jdbcToggleStorage(dataSource)
)