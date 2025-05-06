package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.apikeys.ApiKeysStorageContract
import dev.andrewohara.toggles.projects.ProjectStorageContract
import dev.andrewohara.toggles.toggles.ToggleStorageContract
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

// 9.3.0 hangs for some reason
private fun mysql(): MySQLContainer<*> = MySQLContainer("mysql:8.4.5")
    .withDatabaseName(UUID.randomUUID().toString())

@Testcontainers
class MysqlTogglesStorageTest: ProjectStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}

@Testcontainers
class MysqlProjectStorageTest: ToggleStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}

@Testcontainers
class MysqlApiKeyStorageTest: ApiKeysStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}