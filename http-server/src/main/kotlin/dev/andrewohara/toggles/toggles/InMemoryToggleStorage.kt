package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

internal fun inMemoryToggleStorage() = object: ToggleStorage {

    private val toggles = ConcurrentSkipListSet<Toggle> { o1, o2 ->
        "${o1.projectName}/${o1.toggleName}".compareTo("${o2.projectName}/${o2.toggleName}")
    }

    override fun list(tenantId: TenantId, projectName: ProjectName, pageSize: Int) =
        Paginator<Toggle, ToggleName> { cursor ->
            val page = toggles
                .filter { it.tenantId == tenantId && it.projectName == projectName }
                .sortedBy { "${it.projectName}/${it.toggleName}" }
                .dropWhile { cursor != null && it.toggleName <= cursor }
                .take(pageSize + 1)

            Page(
                items = page.take(pageSize),
                next = page.drop(pageSize).firstOrNull()?.toggleName
            )
        }

    override fun get(tenantId: TenantId, projectName: ProjectName, toggleName: ToggleName): Toggle? {
        return toggles.find { it.tenantId == tenantId && it.projectName == projectName && it.toggleName == toggleName }
    }

    override fun plusAssign(toggle: Toggle) = toggles.plusAssign(toggle)

    override fun minusAssign(toggle: Toggle) = toggles.minusAssign(toggle)
}