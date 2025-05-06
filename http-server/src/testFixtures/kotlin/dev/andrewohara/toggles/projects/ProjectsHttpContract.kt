package dev.andrewohara.toggles.projects

import dev.andrewohara.toggles.ServerContractBase
import dev.andrewohara.toggles.Storage
import dev.andrewohara.toggles.apikeys.generateApiKey
import dev.andrewohara.toggles.dev
import dev.andrewohara.toggles.devAndProd
import dev.andrewohara.toggles.http.ProjectCreateDataDto
import dev.andrewohara.toggles.http.ProjectsPageDto
import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.toggles.idp1Email1
import dev.andrewohara.toggles.inMemory
import dev.andrewohara.toggles.oldNewData
import dev.andrewohara.toggles.projectName1
import dev.andrewohara.toggles.projectName2
import dev.andrewohara.toggles.projectName3
import dev.andrewohara.toggles.tenants.Tenant
import dev.andrewohara.toggles.tenants.TenantCreateData
import dev.andrewohara.toggles.tenants.createTenant
import dev.andrewohara.toggles.toCreate
import dev.andrewohara.toggles.toggleName1
import dev.andrewohara.toggles.toggles.createToggle
import dev.andrewohara.toggles.toggles.deleteToggle
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


abstract class ProjectsHttpContract: ServerContractBase() {

    private lateinit var tenant1: Tenant
    private lateinit var tenant1OwnerToken: String

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = toggles.createTenant(TenantCreateData(idp1Email1)).shouldBeSuccess()
        tenant1OwnerToken = createToken(idp1Email1)
    }

    @Test
    fun `create project - success`() {
        val expected = Project(tenant1.tenantId, projectName1, time, time, devAndProd)

        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)) shouldBeSuccess expected

        httpClient(tenant1OwnerToken).listProjects(null) shouldBeSuccess ProjectsPageDto(
            items = listOf(expected.toDto()),
            next = null
        )
    }
    
    @Test
    fun `create project - already exists`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).createProject(ProjectCreateDataDto(projectName1, devAndProd)) shouldBeFailure TogglesErrorDto(
            message = "Project already exists: $projectName1"
        )
    }

    @Test
    fun `list projects`() {
        val project1 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val project2 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName2, devAndProd)).shouldBeSuccess()
        val project3 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName3, devAndProd)).shouldBeSuccess()

        val page1 = httpClient(tenant1OwnerToken).listProjects(null).shouldBeSuccess()
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = httpClient(tenant1OwnerToken).listProjects(page1.next).shouldBeSuccess()
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(
            project1.toDto(), project2.toDto(), project3.toDto()
        )
    }

    @Test
    fun `delete project - not found`() {
        httpClient(tenant1OwnerToken).deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `delete project - success`() {
        val project1 = toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()
        toggles.deleteToggle(tenant1.tenantId, projectName1, toggleName1).shouldBeSuccess()

        httpClient(tenant1OwnerToken).deleteProject(projectName1) shouldBeSuccess project1.toDto()
        toggles.listProjects(tenant1.tenantId, null) shouldBe Page(
            items = emptyList(),
            next = null
        )
    }

    @Test
    fun `delete project - still has toggles`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not empty: $projectName1"
        )
    }

    @Test
    fun `delete project - still has api keys`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.generateApiKey(tenant1.tenantId, projectName1, dev).shouldBeSuccess()

        httpClient(tenant1OwnerToken).deleteProject(projectName1) shouldBeFailure TogglesErrorDto(
            message = "Project not empty: $projectName1"
        )
    }
}

class InMemoryProjectsHttpTest: ProjectsHttpContract() {
    override fun createStorage() = Storage.inMemory()
}