package dev.andrewohara.toggles

import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

private const val PAGE_SIZE = 2

abstract class TogglesContract {

    protected var time: Instant = Instant.parse("2025-04-24T12:00:00Z")
    private val clock = object: Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: ZoneId?) = error("not implemented")
        override fun instant() = time
    }

    private lateinit var toggles: Toggles
    abstract fun createToggles(clock: Clock, pageSize: Int): Toggles

    @BeforeEach
    fun setup() {
        toggles = createToggles(clock, PAGE_SIZE)
    }

    @Test
    fun `create project - success`() {
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
        toggles.createToggle(projectName1, toggleName1, toggleCreateData).shouldBeSuccess()
        toggles.deleteToggle(projectName1, toggleName1).shouldBeSuccess()

        toggles.deleteProject(projectName1) shouldBeSuccess project1
    }

    @Test
    fun `delete project - not empty`() {

    }
}