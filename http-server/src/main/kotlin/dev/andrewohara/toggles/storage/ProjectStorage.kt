package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.Project
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ProjectNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ProjectStorage {
    fun list(pageSize: Int): Paginator<Project, ProjectName>
    operator fun get(projectName: ProjectName): Project?
    operator fun plusAssign(project: Project)
    operator fun minusAssign(projectName: ProjectName)
}

fun ProjectStorage.getOrFail(projectName: ProjectName) =
    get(projectName).asResultOr { ProjectNotFound(projectName) }