package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ApiKeysStorageContract
import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.toggles.ToggleStorageContract
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private fun postgres(): JdbcDatabaseContainer<*> = PostgreSQLContainer("postgres:17.4")

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

@Testcontainers
class PostgresApiKeyStorageTest: ApiKeysStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}