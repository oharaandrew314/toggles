package dev.andrewohara.toggles

import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import java.time.Instant

data class Project(
    val projectName: ProjectName,
    val createdOn: Instant
): Comparable<Project> {
    override fun compareTo(other: Project) = projectName.compareTo(other.projectName)
}

class ProjectName private constructor(value: String): StringValue(value), ComparableValue<ProjectName, String> {
    companion object: StringValueFactory<ProjectName>(::ProjectName, tokenValidation)
}