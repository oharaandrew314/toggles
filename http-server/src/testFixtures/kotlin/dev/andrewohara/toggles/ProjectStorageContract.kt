package dev.andrewohara.toggles

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

abstract class ProjectStorageContract: StorageContractBase() {

    private lateinit var project1: Project
    private lateinit var project2: Project
    private lateinit var project3: Project

    @BeforeEach
    override fun setup() {
        super.setup()

        project1 = Project(projectName1, t0)
            .also(storage::upsertProject)
        project2 = Project( projectName2, t0 + Duration.ofMinutes(1))
            .also(storage::upsertProject)
        project3 = Project( projectName3, t0 + Duration.ofMinutes(2))
            .also(storage::upsertProject)

        storage.listProjects(100)
            .toList()
            .shouldContainExactlyInAnyOrder(project1, project2, project3)
    }

    @Test
    fun `list project - paged`() {
        val page1 = storage.listProjects(2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.listProjects(2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(project1, project2, project3)
    }

    @Test
    fun `get storage - found`() {
        storage.getProject(projectName2) shouldBe project2
    }

    @Test
    fun `get storage - not found`() {
        storage.getProject(ProjectName.of("missing")) shouldBe null
    }

    @Test
    fun `delete - success`() {
        storage.deleteProject(projectName2)

        storage.listProjects(100)
            .toList()
            .shouldContainExactlyInAnyOrder(project1, project3)
    }

    @Test
    fun `delete - not found`() {
        storage.deleteProject(ProjectName.of("missing"))
    }

    @Test
    fun `save - cannot update`() {
        storage.upsertProject(project1.copy(createdOn = project1.createdOn.plusSeconds(30)))
        storage.listProjects(100).toList().shouldContainExactlyInAnyOrder(project1, project2, project3)
    }
}