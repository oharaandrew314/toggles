package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

fun Storage.Companion.inMemory() = Storage(
    projects = inMemoryProjectStorage(),
    toggles = inMemoryToggleStorage()
)

internal fun inMemoryProjectStorage() = object: ProjectStorage {
    private val projects = ConcurrentSkipListSet<Project>()

    override fun list(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val results = projects.sorted().dropWhile { cursor != null && it.projectName <= cursor }
        val page = results.take(pageSize)
        Page(page, page.lastOrNull()?.projectName?.takeIf { page.size < results.size })
    }

    override fun get(projectName: ProjectName) = projects.find { it.projectName == projectName }

    override fun plusAssign(project: Project) {
        minusAssign(project.projectName)
        projects += project
    }

    override fun minusAssign(projectName: ProjectName) {
        projects.removeIf { it.projectName == projectName }
    }
}

internal fun inMemoryToggleStorage() = object: ToggleStorage {

    private val toggles = ConcurrentSkipListSet<Toggle>()

    override fun list(projectName: ProjectName, pageSize: Int) = Paginator<Toggle, ToggleName> { cursor ->
        val results = toggles
            .filter { it.projectName == projectName }
            .sorted()
            .dropWhile { cursor != null && it.toggleName <= cursor }

        val page = results.take(pageSize)
        Page(page, page.lastOrNull()?.toggleName?.takeIf { page.size < results.size })
    }

    override fun get(projectName: ProjectName, toggleName: ToggleName): Toggle? {
        return toggles.find { it.projectName == projectName && it.toggleName == toggleName }
    }

    override fun plusAssign(toggle: Toggle) {
        remove(toggle.projectName, toggle.toggleName)
        toggles += toggle
    }

    override fun remove(projectName: ProjectName, toggleName: ToggleName) {
        toggles.removeIf { it.projectName == projectName && it.toggleName == toggleName }
    }
}