package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class PostgresJdbcProjectStorageTest: ProjectStorageContract() {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17.4")
            .withInitScript("init.sql")
    }

    override fun createStorage() = postgres
        .toDataSource()
        .truncate()
        .let { JdbcProjectStorage(it) }
}