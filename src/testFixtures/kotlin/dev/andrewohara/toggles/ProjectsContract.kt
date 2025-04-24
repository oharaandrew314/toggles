package dev.andrewohara.toggles

import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

abstract class ProjectsContract: ContractBase() {

    @Test
    fun `create project - success`() {
        val expected = Project(projectName1, time)

        toggles.createProject(projectName1) shouldBeSuccess expected
        toggles.listProjects(null) shouldBe Page(
            items = listOf(expected),
            next = null
        )
    }
    
    @Test
    fun `create project - already exists`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.createProject(projectName1) shouldBeFailure ProjectAlreadyExists(projectName1)
    }

    @Test
    fun `list projects`() {
        val project1 = toggles.createProject(projectName1).shouldBeSuccess()
        val project2 = toggles.createProject(projectName2).shouldBeSuccess()
        val project3 = toggles.createProject(projectName3).shouldBeSuccess()

        toggles.listProjects(null) shouldBe Page(
            items = listOf(project1, project2),
            next = project2.projectName
        )

        toggles.listProjects(project2.projectName) shouldBe Page(
            items = listOf(project3),
            next = null
        )
    }

    @Test
    fun `delete project - not found`() {
        toggles.deleteProject(projectName1) shouldBeFailure ProjectNotFound(projectName1)
    }

    @Test
    fun `delete project - success`() {
        val project1 = toggles.createProject(projectName1).shouldBeSuccess()
        toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()
        toggles.deleteToggle(projectName1, toggleName1).shouldBeSuccess()

        toggles.deleteProject(projectName1) shouldBeSuccess project1
        toggles.listProjects(null) shouldBe Page(
            items = emptyList(),
            next = null
        )
    }

    @Test
    fun `delete project - not empty`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()

        toggles.deleteProject(projectName1) shouldBeFailure ProjectNotEmpty(projectName1)
    }
}