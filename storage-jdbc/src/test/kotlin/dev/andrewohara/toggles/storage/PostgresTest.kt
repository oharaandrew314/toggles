package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.apikeys.ApiKeysStorageContract
import dev.andrewohara.toggles.projects.ProjectStorageContract
import dev.andrewohara.toggles.tenants.TenantStorageContract
import dev.andrewohara.toggles.toggles.ToggleStorageContract
import dev.andrewohara.toggles.users.UserStorageContract
import org.junit.jupiter.api.Tag
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private fun postgres(): JdbcDatabaseContainer<*> = PostgreSQLContainer("postgres:17.4")

@Tag("slow")
@Testcontainers
class PostgresProjectStorageTest: ProjectStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}

@Tag("slow")
@Testcontainers
class PostgresToggleStorageTest: ToggleStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}

@Tag("slow")
@Testcontainers
class PostgresApiKeyStorageTest: ApiKeysStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}

@Tag("slow")
@Testcontainers
class PostgresTenantStorageTest: TenantStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}

@Tag("slow")
@Testcontainers
class PostgresUserStorageTest: UserStorageContract() {

    companion object {
        @Container @JvmStatic val postgres = postgres()
    }

    override fun createStorage() = postgres.toStorage()
}