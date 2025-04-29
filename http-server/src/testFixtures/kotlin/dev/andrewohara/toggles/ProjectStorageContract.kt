package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.ProjectStorage
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

abstract class ProjectStorageContract {

    private val t0 = Instant.parse("2025-04-29T12:00:00Z")

    private lateinit var projectStorage: ProjectStorage
    abstract fun createProjectRepo(): ProjectStorage

    private lateinit var project1: Project
    private lateinit var project2: Project
    private lateinit var project3: Project

    @BeforeEach
    fun setup() {
        projectStorage = createProjectRepo()
        project1 = Project(projectName1, t0)
            .also(projectStorage::plusAssign)
        project2 = Project( projectName2, t0 + Duration.ofMinutes(1))
            .also(projectStorage::plusAssign)
        project3 = Project( projectName3, t0 + Duration.ofMinutes(2))
            .also(projectStorage::plusAssign)
    }

    @Test
    fun `list project - paged`() {
        val page1 = projectStorage.list(2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = projectStorage.list(2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(project1, project2, project3)
    }

    @Test
    fun `get storage - found`() {
        projectStorage[projectName2] shouldBe project2
    }

    @Test
    fun `get storage - not found`() {
        projectStorage[ProjectName.of("missing")] shouldBe null
    }

    @Test
    fun `delete - success`() {
        projectStorage.delete(projectName2) shouldBe project2

        projectStorage.list(100)
            .toList()
            .shouldContainExactlyInAnyOrder(project1, project3)
    }

    @Test
    fun `delete - not found`() {
        projectStorage.delete(ProjectName.of("missing")) shouldBe null
    }
}