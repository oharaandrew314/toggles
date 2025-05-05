package dev.andrewohara.toggles.storage

import org.testcontainers.containers.JdbcDatabaseContainer
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.andrewohara.toggles.Storage

fun JdbcDatabaseContainer<*>.toStorage() = HikariConfig().also {
        it.jdbcUrl = jdbcUrl
        it.username = username
        it.password = password
    }
    .let(::HikariDataSource)
    .let(Storage::jdbc)