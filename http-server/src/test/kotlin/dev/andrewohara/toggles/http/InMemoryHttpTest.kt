package dev.andrewohara.toggles.http

import dev.andrewohara.toggles.Storage
import dev.andrewohara.toggles.inMemory
import dev.andrewohara.toggles.projects.ProjectsHttpContract
import dev.andrewohara.toggles.toggles.TogglesHttpContract

class InMemoryProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = Storage.inMemory()
}

class InMemoryTogglesHttpTest: TogglesHttpContract() {
    override fun createStorage() = Storage.inMemory()
}