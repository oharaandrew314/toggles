package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class MysqlJdbcProjectStorageTest: ProjectStorageContract() {

    companion object {
        @Container
        @JvmStatic
        val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.4.5") // 9.3.0 hangs for some reason
            .withInitScript("init.sql")
    }

    override fun createStorage() = mysql
        .toDataSource()
        .truncate()
        .let { JdbcProjectStorage(it) }
}