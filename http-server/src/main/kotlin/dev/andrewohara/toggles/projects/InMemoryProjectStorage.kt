package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

internal fun inMemoryProjectStorage() = object: ProjectStorage {
    private val comparator = Comparator<Project> { o1, o2 ->
        "${o1.tenantId}/${o1.projectName}".compareTo("${o2.tenantId}/${o2.projectName}")
    }

    private val projects = ConcurrentSkipListSet(comparator)

    override fun list(tenantId: TenantId, pageSize: Int) = Paginator<Project, ProjectName> { cursor ->
        val page = projects
            .filter { it.tenantId == tenantId }
            .sortedWith(comparator)
            .dropWhile { cursor != null && it.projectName < cursor }
            .take(pageSize + 1)

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.projectName
        )
    }

    override fun get(tenantId: TenantId, projectName: ProjectName) = projects
        .find { it.tenantId == tenantId && it.projectName == projectName }

    override fun plusAssign(project: Project) {
        this -= project
        projects += project
    }

    override fun minusAssign(project: Project) {
        projects.removeIf { it.tenantId == project.tenantId && it.projectName == project.projectName }
    }
}