package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.apikeys.ApiKeysStorageContract
import dev.andrewohara.toggles.projects.ProjectStorageContract
import dev.andrewohara.toggles.tenants.TenantStorageContract
import dev.andrewohara.toggles.toggles.ToggleStorageContract
import dev.andrewohara.toggles.users.UserStorageContract
import org.junit.jupiter.api.Tag
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

// 9.3.0 hangs for some reason
private fun mysql(): MySQLContainer<*> = MySQLContainer("mysql:8.4.5")
    .withDatabaseName(UUID.randomUUID().toString())

@Tag("slow")
@Testcontainers
class MysqlTogglesStorageTest: ProjectStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}

@Tag("slow")
@Testcontainers
class MysqlProjectStorageTest: ToggleStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}

@Tag("slow")
@Testcontainers
class MysqlApiKeyStorageTest: ApiKeysStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}

@Tag("slow")
@Testcontainers
class MysqlTenantStorageTest: TenantStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}

@Tag("slow")
@Testcontainers
class MysqlUserStorageTest: UserStorageContract() {

    companion object {
        @Container @JvmStatic val mysql = mysql()
    }

    override fun createStorage() = mysql.toStorage()
}