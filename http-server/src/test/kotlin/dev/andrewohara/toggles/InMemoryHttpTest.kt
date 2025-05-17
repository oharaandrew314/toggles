package dev.andrewohara.toggles

import dev.andrewohara.toggles.projects.ProjectsHttpContract
import dev.andrewohara.toggles.tenants.Tenant
import dev.andrewohara.toggles.tenants.TenantsHttpContract
import dev.andrewohara.toggles.toggles.TogglesHttpContract
import dev.andrewohara.toggles.users.UsersHttpContract

class InMemoryProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryTogglesHttpTest: TogglesHttpContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryUsersHttpTest: UsersHttpContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryTenantsHttpTest: TenantsHttpContract() {
    override fun createStorage() = Storage.inMemory()
}