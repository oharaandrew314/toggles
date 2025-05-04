package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.toggles.ToggleStorageContract
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private fun postgres(): JdbcDatabaseContainer<*> = PostgreSQLContainer<Nothing>("postgres:17.4")
    .withInitScript("init.sql")

@Testcontainers
class PostgresProjectStorageTest: ProjectStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}

@Testcontainers
class PostgresToggleStorageTest: ToggleStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}