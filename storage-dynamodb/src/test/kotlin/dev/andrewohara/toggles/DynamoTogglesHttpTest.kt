package dev.andrewohara.toggles

import dev.andrewohara.toggles.projects.ProjectsHttpContract
import dev.andrewohara.toggles.storage.testDynamoStorage
import dev.andrewohara.toggles.toggles.TogglesHttpContract

class DynamoTogglesHttpTest: TogglesHttpContract() {
    override fun createStorage() = testDynamoStorage()
}

class DynamoProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = testDynamoStorage()
}
