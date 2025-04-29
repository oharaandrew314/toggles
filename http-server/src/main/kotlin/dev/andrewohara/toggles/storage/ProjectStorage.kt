package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ProjectStorage {
    companion object

    fun list(pageSize: Int): Paginator<Project, ProjectName>
    operator fun get(projectName: ProjectName): Project?
    operator fun plusAssign(project: Project)
    operator fun minusAssign(project: Project)
    fun delete(projectName: ProjectName): Project? = get(projectName)?.also(::minusAssign)
}

fun ProjectStorage.getOrFail(projectName: ProjectName) =
    get(projectName).asResultOr { ProjectNotFound(projectName) }