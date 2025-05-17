package dev.andrewohara.toggles

import dev.andrewohara.toggles.projects.ProjectsHttpContract
import dev.andrewohara.toggles.storage.testDynamoStorage
import dev.andrewohara.toggles.tenants.TenantsHttpContract
import dev.andrewohara.toggles.toggles.TogglesHttpContract
import dev.andrewohara.toggles.users.UsersHttpContract

class DynamoTogglesHttpTest: TogglesHttpContract() {
    override fun createStorage() = testDynamoStorage()
}

class DynamoProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = testDynamoStorage()
}

class DynamoTenantsHttpTest: TenantsHttpContract() {
    override fun createStorage() = testDynamoStorage()
}

class DynamoUsersHttpTest: UsersHttpContract() {
    override fun createStorage() = testDynamoStorage()
}