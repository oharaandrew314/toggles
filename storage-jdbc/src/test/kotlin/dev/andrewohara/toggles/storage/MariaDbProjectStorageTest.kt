package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ApiKeysStorageContract
import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.toggles.ToggleStorageContract
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

// 9.3.0 hangs for some reason
private fun mariaDb(): MariaDBContainer<*> = MariaDBContainer("mariadb:11.7.2")

@Testcontainers
class MariDbTogglesStorageTest: ProjectStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}

@Testcontainers
class MariaDbProjectStorageTest: ToggleStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}

@Testcontainers
class MariaDbApiKeyStorageTest: ApiKeysStorageContract() {

    companion object {
        @Container @JvmStatic val mariaDb = mariaDb()
    }

    override fun createStorage() = mariaDb.toStorage()
}