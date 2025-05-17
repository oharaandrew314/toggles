package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ServerContractBase
import dev.andrewohara.toggles.apikeys.generateApiKey
import dev.andrewohara.toggles.dev
import dev.andrewohara.toggles.devAndProd
import dev.andrewohara.toggles.TogglesErrorDto
import dev.andrewohara.toggles.idp2Email1
import dev.andrewohara.toggles.oldNewData
import dev.andrewohara.toggles.projectName1
import dev.andrewohara.toggles.projectName2
import dev.andrewohara.toggles.projectName3
import dev.andrewohara.toggles.toCreate
import dev.andrewohara.toggles.toggleName1
import dev.andrewohara.toggles.toggles.createToggle
import dev.andrewohara.toggles.toggles.deleteToggle
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

abstract class ProjectsHttpContract: ServerContractBase() {

    @Test
    fun `create project - success as admin`() {
        val expected = Project(tenant1.tenantId, projectName1, time, time, devAndProd)

        httpClient(adminToken)
            .createProject(ProjectCreateDataDto(projectName1, devAndProd))
            .shouldBeSuccess(expected.toDto())

        storage.projects.list(tenant1.tenantId, 100).toList().shouldContainExactly(expected)
    }

    @Test
    fun `create project - success as developer`() {
        val expected = Project(tenant1.tenantId, projectName1, time, time, devAndProd)

        httpClient(developerToken)
            .createProject(ProjectCreateDataDto(projectName1, devAndProd))
            .shouldBeSuccess(expected.toDto())

        storage.projects.list(tenant1.tenantId, 100).toList().shouldContainExactly(expected)
    }

    @Test
    fun `create project - forbidden as tester`() {
        httpClient(testerToken)
            .createProject(ProjectCreateDataDto(projectName1, devAndProd))
            .shouldBeFailure(TogglesErrorDto("You must be an admin or developer to perform this action"))
    }
    
    @Test
    fun `create project - already exists`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(adminToken).createProject(ProjectCreateDataDto(projectName1, devAndProd)) shouldBeFailure TogglesErrorDto(
            message = "Project already exists: $projectName1"
        )
    }

    @Test
    fun `list projects - success as admin`() {
        val project1 = toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val project2 = toggles.createProject(admin, ProjectCreateData(projectName2, devAndProd)).shouldBeSuccess()
        val project3 = toggles.createProject(admin, ProjectCreateData(projectName3, devAndProd)).shouldBeSuccess()

        val page1 = httpClient(adminToken).listProjects(null).shouldBeSuccess()
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = httpClient(adminToken).listProjects(page1.next).shouldBeSuccess()
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(
            project1.toDto(), project2.toDto(), project3.toDto()
        )
    }

    @Test
    fun `list projects - logged in but not registered`() {
        val token = createToken(idp2Email1)

        httpClient(token).listProjects(null) shouldBeFailure TogglesErrorDto(
            message = "You do not belong to a tenant"
        )
    }

    @Test
    fun `list projects - success as developer`() {
        val project = toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(developerToken).listProjects(null) shouldBeSuccess ProjectsPageDto(
            items = listOf(project.toDto()),
            next = null
        )
    }

    @Test
    fun `list projects - success as tester`() {
        val project = toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(testerToken).listProjects(null) shouldBeSuccess ProjectsPageDto(
            items = listOf(project.toDto()),
            next = null
        )
    }

    @Test
    fun `delete project - not found`() {
        httpClient(adminToken).deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `delete project - success as admin`() {
        val project1 = toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()
        toggles.deleteToggle(admin, projectName1, toggleName1).shouldBeSuccess()

        httpClient(adminToken).deleteProject(projectName1) shouldBeSuccess project1.toDto()
        toggles.listProjects(tenant1.tenantId, null) shouldBe Page(
            items = emptyList(),
            next = null
        )
    }

    @Test
    fun `delete project - success as developer`() {
        val project1 = toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        httpClient(developerToken).deleteProject(projectName1) shouldBeSuccess project1.toDto()
    }

    @Test
    fun `delete project - forbidden as tester`() {
        val project1 = toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        httpClient(testerToken)
            .deleteProject(project1.projectName)
            .shouldBeFailure(TogglesErrorDto("You must be an admin or developer to perform this action"))
    }

    @Test
    fun `delete project - still has toggles`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(adminToken).deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not empty: $projectName1"
        )
    }

    @Test
    fun `delete project - still has api keys`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.generateApiKey(tenant1.tenantId, projectName1, dev).shouldBeSuccess()

        httpClient(adminToken).deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not empty: $projectName1"
        )
    }
}