package dev.andrewohara.toggles

import java.time.Instant

data class Project(
    val projectName: ProjectName,
    val createdOn: Instant
): Comparable<Project> {
    override fun compareTo(other: Project) = projectName.compareTo(other.projectName)
}