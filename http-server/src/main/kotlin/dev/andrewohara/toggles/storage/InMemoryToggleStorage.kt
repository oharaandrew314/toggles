package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

fun ToggleStorage.Companion.inMemory() = object: ToggleStorage {

    private val toggles = ConcurrentSkipListSet<Toggle>()
    private val projects = ConcurrentSkipListSet<Project>()

    // Toggles

    override fun listToggles(projectName: ProjectName, pageSize: Int) = Paginator<Toggle, ToggleName> { cursor ->
        val results = toggles
            .filter { it.projectName == projectName }
            .sorted()
            .dropWhile { cursor != null && it.toggleName <= cursor }

        val page = results.take(pageSize)
        Page(page, page.lastOrNull()?.toggleName?.takeIf { page.size < results.size })
    }

    override fun getToggle(projectName: ProjectName, toggleName: ToggleName): Toggle? {
        return toggles.find { it.projectName == projectName && it.toggleName == toggleName }
    }

    override fun upsertToggle(toggle: Toggle) {
        deleteToggle(toggle.projectName, toggle.toggleName)
        toggles += toggle
    }

    override fun deleteToggle(projectName: ProjectName, toggleName: ToggleName) {
        toggles.removeIf { it.projectName == projectName && it.toggleName == toggleName }
    }

    // Projects

    override fun listProjects(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val results = projects.sorted().dropWhile { cursor != null && it.projectName <= cursor }
        val page = results.take(pageSize)
        Page(page, page.lastOrNull()?.projectName?.takeIf { page.size < results.size })
    }

    override fun getProject(projectName: ProjectName) = projects.find { it.projectName == projectName }

    override fun upsertProject(project: Project) = projects.plusAssign(project)

    override fun deleteProject(projectName: ProjectName) {
        projects.removeIf { it.projectName == projectName }
    }

}