package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.ProjectDataDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

abstract class ProjectsHttpContract: ContractBase() {

    @Test
    fun `create project - success`() {
        val expected = Project(projectName1, time)

        toggles.createProject(ProjectData(projectName1)) shouldBeSuccess expected

        httpClient.listProjects(null) shouldBeSuccess ProjectsPageDto(
            items = listOf(expected.toDto()),
            next = null
        )
    }
    
    @Test
    fun `create project - already exists`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()

        httpClient.createProject(ProjectDataDto(projectName1)) shouldBeFailure TogglesErrorDto(
            message = "Project already exists: $projectName1"
        )
    }

    @Test
    fun `list projects`() {
        val project1 = toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        val project2 = toggles.createProject(ProjectData(projectName2)).shouldBeSuccess()
        val project3 = toggles.createProject(ProjectData(projectName3)).shouldBeSuccess()

        httpClient.listProjects(null) shouldBeSuccess ProjectsPageDto(
            items = listOf(project1.toDto(), project2.toDto()),
            next = project2.projectName
        )

        httpClient.listProjects(project2.projectName) shouldBeSuccess ProjectsPageDto(
            items = listOf(project3.toDto()),
            next = null
        )
    }

    @Test
    fun `delete project - not found`() {
        httpClient.deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `delete project - success`() {
        val project1 = toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()
        toggles.deleteToggle(projectName1, toggleName1).shouldBeSuccess()

        httpClient.deleteProject(projectName1) shouldBeSuccess project1.toDto()
        toggles.listProjects(null) shouldBe Page(
            items = emptyList(),
            next = null
        )
    }

    @Test
    fun `delete project - not empty`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()

        httpClient.deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not empty: $projectName1"
        )
    }
}