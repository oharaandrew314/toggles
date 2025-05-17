package dev.andrewohara.toggles.storage

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.andrewohara.toggles.apikeys.ApiKeysStorageContract
import dev.andrewohara.toggles.projects.ProjectStorageContract
import dev.andrewohara.toggles.projects.ProjectsHttpContract
import dev.andrewohara.toggles.Storage
import dev.andrewohara.toggles.tenants.TenantStorageContract
import dev.andrewohara.toggles.toggles.ToggleStorageContract
import dev.andrewohara.toggles.toggles.TogglesHttpContract
import dev.andrewohara.toggles.users.UsersHttpContract
import dev.andrewohara.toggles.users.UserStorageContract
import java.util.UUID

private fun create() = HikariConfig()
    .also { it.jdbcUrl = "jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1" }
    .let(::HikariDataSource)
    .let { Storage.jdbc(it) }

class H2ToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = create()
}

class H2ProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = create()
}

class H2ApiKeyStorageTest: ApiKeysStorageContract() {
    override fun createStorage() = create()
}

class H2TenantStorageTest: TenantStorageContract() {
    override fun createStorage() = create()
}

class H2UserStorageTest: UserStorageContract() {
    override fun createStorage() = create()
}

class H2TogglesHttpTest: TogglesHttpContract() {
    override fun createStorage() = create()
}

class H2ProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = create()
}

class H2UsersHttpTest: UsersHttpContract() {
    override fun createStorage() = create()
}

class H2TenantsHttpTest: TenantStorageContract() {
    override fun createStorage() = create()
}