package dev.andrewohara.toggles.repo

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

fun ProjectRepo.Companion.inMemory() = object: ProjectRepo {
    private val projects = ConcurrentSkipListSet<Project>()

    override fun list(pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val results = projects.sorted().dropWhile { cursor != null && it.projectName <= cursor }
        val page = results.take(pageSize)
        Page(page, page.lastOrNull()?.projectName?.takeIf { page.size < results.size })
    }

    override fun get(projectName: ProjectName) = projects.find { it.projectName == projectName }
    override fun plusAssign(project: Project) = projects.plusAssign(project)
    override fun minusAssign(project: Project) = projects.minusAssign(project)
    override fun delete(projectName: ProjectName): Project? {
        return get(projectName)?.also { projects -= it }
    }
}