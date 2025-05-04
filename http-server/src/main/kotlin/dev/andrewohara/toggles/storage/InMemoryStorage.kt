package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.Toggle
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.TokenMd5
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet

fun Storage.Companion.inMemory() = Storage(
    projects = inMemoryProjectStorage(),
    toggles = inMemoryToggleStorage(),
    apiKeys = apiKeyStorage()
)

internal fun inMemoryProjectStorage() = object: ProjectStorage {
    private val projects = ConcurrentSkipListMap< ProjectName, Project>()

    override fun list(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val results = projects
            .values
            .sortedBy { it.projectName }
            .dropWhile { cursor != null && it.projectName <= cursor }

        val page = results.take(pageSize)
        Page(page, page.lastOrNull()?.projectName?.takeIf { page.size < results.size })
    }

    override fun get(projectName: ProjectName) = projects[projectName]

    override fun plusAssign(project: Project) {
        projects -= project.projectName
        projects[project.projectName] = project
    }

    override fun minusAssign(projectName: ProjectName) {
        projects -= projectName
    }
}

internal fun inMemoryToggleStorage() = object: ToggleStorage {

    private val toggles = ConcurrentSkipListSet<Toggle> { o1, o2 ->
        "${o1.projectName}/${o1.toggleName}".compareTo("${o2.projectName}/${o2.toggleName}")
    }

    override fun list(projectName: ProjectName, pageSize: Int) = Paginator<Toggle, ToggleName> { cursor ->
        val results = toggles
            .filter { it.projectName == projectName }
            .sortedBy { "${it.projectName}/${it.toggleName}" }
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

internal fun apiKeyStorage() = object: ApiKeyStorage {
    private val keys = ConcurrentSkipListMap<ApiKeyMeta, TokenMd5> { o1, o2 ->
        "${o1.projectName}/${o1.environment}".compareTo("${o2.projectName}/${o2.environment}")
    }

    override fun list(projectName: ProjectName, pageSize: Int) = Paginator<ApiKeyMeta, EnvironmentName> { cursor ->
        val results = keys
            .entries
            .filter { it.key.projectName == projectName }
            .map { it.key }
            .sortedBy { it.environment }
            .dropWhile { cursor != null && it.environment <= cursor }

        val page = results.take(pageSize)
        Page(
            page,
            page.lastOrNull()?.environment?.takeIf { page.size < results.size }
        )
    }

    override fun get(projectName: ProjectName, environment: EnvironmentName) =
        keys.keys.find { it.projectName == projectName && it.environment == environment }

    override fun set(meta: ApiKeyMeta, tokenMd5: TokenMd5) {
        keys[meta] = tokenMd5
    }

    override fun minusAssign(meta: ApiKeyMeta) = keys.minusAssign(meta)

    override fun get(tokenMd5: TokenMd5) = keys.entries
        .find { it.value == tokenMd5 }
        ?.key
}