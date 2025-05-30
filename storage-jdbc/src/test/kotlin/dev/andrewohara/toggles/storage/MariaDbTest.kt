package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.apikeys.ApiKeysStorageContract
import dev.andrewohara.toggles.projects.ProjectStorageContract
import dev.andrewohara.toggles.tenants.TenantStorageContract
import dev.andrewohara.toggles.toggles.ToggleStorageContract
import dev.andrewohara.toggles.users.UserStorageContract
import org.junit.jupiter.api.Tag
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

private fun mariaDb(): MariaDBContainer<*> = MariaDBContainer("mariadb:11.7.2")

@Tag("slow")
@Testcontainers
class MariaDbToggleStorageTest: ProjectStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}

@Tag("slow")
@Testcontainers
class MariaDbProjectStorageTest: ToggleStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}

@Tag("slow")
@Testcontainers
class MariaDbApiKeyStorageTest: ApiKeysStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}

@Tag("slow")
@Testcontainers
class MariaDbTenantStorageTest: TenantStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}

@Tag("slow")
@Testcontainers
class MariaDbUserStorageTest: UserStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}