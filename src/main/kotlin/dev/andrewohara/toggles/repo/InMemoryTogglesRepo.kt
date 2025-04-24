package dev.andrewohara.toggles.repo

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

fun TogglesRepo.Companion.inMemory() = object: TogglesRepo {

    private val toggles = ConcurrentSkipListSet<Toggle>()

    override fun list(projectName: ProjectName, pageSize: Int) = Paginator<Toggle, ToggleName> { cursor ->
        val results = toggles.sorted().dropWhile { cursor != null && it.toggleName <= cursor }
        val page = results.take(pageSize)
        Page(page, page.lastOrNull()?.toggleName?.takeIf { page.size < results.size })
    }

    override fun get(projectName: ProjectName, toggleName: ToggleName): Toggle? {
        return toggles.find { it.projectName == projectName && it.toggleName == toggleName }
    }

    override fun plusAssign(toggle: Toggle) {
        minusAssign(toggle)
        toggles += toggle
    }

    override fun minusAssign(toggle: Toggle) {
        toggles.removeIf { it.projectName == toggle.projectName && it.toggleName == toggle.toggleName }
    }

    override fun delete(projectName: ProjectName, toggleName: ToggleName): Toggle? {
        return get(projectName, toggleName)
            ?.also(::minusAssign)
    }
}