package dev.andrewohara.toggles.storage

import org.testcontainers.containers.JdbcDatabaseContainer
import javax.sql.DataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource


fun DataSource.executeResource(resourceName: String) {
    val sql = javaClass.classLoader.getResourceAsStream(resourceName)!!.reader().readText()
    execute(sql)
}

fun DataSource.execute(sql: String) {
    connection.use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            stmt.execute()
        }
    }
}

fun DataSource.truncate() = apply {
    execute("TRUNCATE TABLE projects")
    execute("TRUNCATE TABLE toggles")
}

fun JdbcDatabaseContainer<*>.toDataSource(): DataSource {
    val config = HikariConfig().also {
        it.jdbcUrl = jdbcUrl
        it.username = username
        it.password = password
    }
    return HikariDataSource(config)
}