package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.ServerContractBase
import dev.andrewohara.toggles.dev
import dev.andrewohara.toggles.devAndProd
import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.http.TogglesPageDto
import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.toggles.idp1Email1
import dev.andrewohara.toggles.mostlyNew
import dev.andrewohara.toggles.mostlyOld
import dev.andrewohara.toggles.oldNewData
import dev.andrewohara.toggles.onOffData
import dev.andrewohara.toggles.prod
import dev.andrewohara.toggles.projectName1
import dev.andrewohara.toggles.projectName2
import dev.andrewohara.toggles.projects.ProjectCreateData
import dev.andrewohara.toggles.projects.createProject
import dev.andrewohara.toggles.tenants.Tenant
import dev.andrewohara.toggles.tenants.TenantCreateData
import dev.andrewohara.toggles.tenants.createTenant
import dev.andrewohara.toggles.toCreate
import dev.andrewohara.toggles.toDto
import dev.andrewohara.toggles.toggleName1
import dev.andrewohara.toggles.toggleName2
import dev.andrewohara.toggles.toggleName3
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

abstract class TogglesHttpContract: ServerContractBase() {

    private lateinit var tenant1: Tenant
    private lateinit var tenant1OwnerToken: String

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = toggles.createTenant(TenantCreateData(idp1Email1)).shouldBeSuccess()
        tenant1OwnerToken = createToken(idp1Email1)
    }
    
    @Test
    fun `create toggle - project not found`() {
        httpClient(tenant1OwnerToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }
    
    @Test
    fun `create toggle - already exists`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle already exists: ${projectName1}/${toggleName1}"
        )
    }
    
    @Test
    fun `create toggle - success`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        val expected = Toggle(
            tenantId = tenant1.tenantId,
            projectName = projectName1,
            toggleName = toggleName1,
            variations = oldNewData.variations,
            defaultVariation = oldNewData.defaultVariation,
            createdOn = time,
            updatedOn = time,
            environments = mapOf(
                dev to mostlyNew,
                prod to mostlyOld
            )
        )

        httpClient(tenant1OwnerToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ) shouldBeSuccess expected.toDto()

        toggles.listToggles(tenant1.tenantId, projectName1, null) shouldBeSuccess Page(
            items = listOf(expected),
            next = null
        )
    }

    @Test
    fun `create toggle - duplicate in other project`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName2, devAndProd)).shouldBeSuccess()
        val toggle1 = toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        val toggle2 = httpClient(tenant1OwnerToken).createToggle(
            projectName = projectName2,
            data = oldNewData.toCreate(toggleName1).toDto()
        ).shouldBeSuccess()

        toggles.listToggles(tenant1.tenantId, projectName1, null) shouldBeSuccess Page(listOf(toggle1), null)
        httpClient(tenant1OwnerToken).listToggles(projectName2, null) shouldBeSuccess TogglesPageDto(listOf(toggle2), null)
    }
    
    @Test
    fun `list toggles - success`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName2, devAndProd)).shouldBeSuccess()

        val toggle1 = toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()
        val toggle2 = toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName2)).shouldBeSuccess()
        val toggle3 = toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName3)).shouldBeSuccess()
        val toggle4 = toggles.createToggle(tenant1.tenantId, projectName2, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        val page1 = httpClient(tenant1OwnerToken).listToggles(projectName1, null).shouldBeSuccess()
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = httpClient(tenant1OwnerToken).listToggles(projectName1, page1.next).shouldBeSuccess()
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(
            toggle1.toDto(), toggle2.toDto(), toggle3.toDto()
        )

        httpClient(tenant1OwnerToken).listToggles(projectName2, null) shouldBeSuccess TogglesPageDto(
            items = listOf(toggle4.toDto()),
            next = null
        )
    }
    
    @Test
    fun `update toggle - toggle not found`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = oldNewData.toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: ${projectName1}/${toggleName1}"
        )
    }
    
    @Test
    fun `update toggle - project not found`() {
        httpClient(tenant1OwnerToken).updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = oldNewData.toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }
    
    @Test
    fun `update toggle - success`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val original = toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        time += Duration.ofSeconds(5)

        val expected = original.copy(
            variations = onOffData.variations,
            defaultVariation = onOffData.defaultVariation,
            environments = onOffData.environments,
            updatedOn = time
        )

        httpClient(tenant1OwnerToken).updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = onOffData.toDto()
        ) shouldBeSuccess expected.toDto()

        toggles.listToggles(tenant1.tenantId, projectName1, null) shouldBeSuccess Page(
            items = listOf(expected),
            next = null
        )
    }
    
    @Test
    fun `get toggle - project not found`() {
        httpClient(tenant1OwnerToken).getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `get toggle - toggle not found`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: ${projectName1}/${toggleName1}"
        )
    }

    @Test
    fun `get toggle - success`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        val toggle = toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeSuccess toggle.toDto()
    }

    @Test
    fun `delete toggle - project not found`() {
        httpClient(tenant1OwnerToken).deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `delete toggle - toggle not found`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: ${projectName1}/${toggleName1}"
        )
    }

    @Test
    fun `delete toggle - success`() {
        toggles.createProject(tenant1.tenantId, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val toggle = toggles.createToggle(tenant1.tenantId, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(tenant1OwnerToken).deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeSuccess toggle.toDto()

        toggles.listToggles(tenant1.tenantId, projectName1, null) shouldBeSuccess Page(
            items = emptyList(),
            next = null
        )
    }
}