package dev.andrewohara.toggles.toggles

import dev.andrewohara.toggles.ServerContractBase
import dev.andrewohara.toggles.dev
import dev.andrewohara.toggles.devAndProd
import dev.andrewohara.toggles.mostlyNew
import dev.andrewohara.toggles.mostlyOld
import dev.andrewohara.toggles.oldNewData
import dev.andrewohara.toggles.onOffData
import dev.andrewohara.toggles.prod
import dev.andrewohara.toggles.projectName1
import dev.andrewohara.toggles.projectName2
import dev.andrewohara.toggles.projects.ProjectCreateData
import dev.andrewohara.toggles.projects.createProject
import dev.andrewohara.toggles.toCreate
import dev.andrewohara.toggles.toDto
import dev.andrewohara.toggles.toggleName1
import dev.andrewohara.toggles.toggleName2
import dev.andrewohara.toggles.toggleName3
import dev.andrewohara.toggles.TogglesErrorDto
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.map
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import dev.forkhandles.result4k.map
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import java.time.Duration

abstract class TogglesHttpContract: ServerContractBase() {
    
    @Test
    fun `create toggle - project not found`() {
        httpClient(adminToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }
    
    @Test
    fun `create toggle - already exists`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(adminToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle already exists: ${projectName1}/${toggleName1}"
        )
    }
    
    @Test
    fun `create toggle - success as admin`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

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

        httpClient(adminToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ) shouldBeSuccess expected.toDto()

        toggles.listToggles(tenant1.tenantId, projectName1, null) shouldBeSuccess Page(
            items = listOf(expected),
            next = null
        )
    }

    @Test
    fun `create toggle - success as developer`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        val toggle = httpClient(developerToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ).shouldBeSuccess()

        toggles.listToggles(tenant1.tenantId, projectName1, null)
            .map { it.map(Toggle::toDto) } shouldBeSuccess Page(
            items = listOf(toggle),
            next = null
        )
    }

    @Test
    fun `create toggle - forbidden as tester`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(testerToken).createToggle(
            projectName = projectName1,
            data = oldNewData.toCreate(toggleName1).toDto()
        ).shouldBeFailure(TogglesErrorDto("You must be an admin or developer to perform this action"))
    }

    @Test
    fun `create toggle - duplicate in other project`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createProject(admin, ProjectCreateData(projectName2, devAndProd)).shouldBeSuccess()
        val toggle1 = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        val toggle2 = httpClient(adminToken).createToggle(
            projectName = projectName2,
            data = oldNewData.toCreate(toggleName1).toDto()
        ).shouldBeSuccess()

        toggles.listToggles(tenant1.tenantId, projectName1, null) shouldBeSuccess Page(listOf(toggle1), null)
        httpClient(adminToken).listToggles(projectName2, null) shouldBeSuccess TogglesPageDto(
            listOf(toggle2),
            null
        )
    }
    
    @Test
    fun `list toggles - success`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        toggles.createProject(admin, ProjectCreateData(projectName2, devAndProd)).shouldBeSuccess()

        val toggle1 = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()
        val toggle2 = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName2)).shouldBeSuccess()
        val toggle3 = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName3)).shouldBeSuccess()
        val toggle4 = toggles.createToggle(admin, projectName2, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        val page1 = httpClient(testerToken).listToggles(projectName1, null).shouldBeSuccess()
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = httpClient(testerToken).listToggles(projectName1, page1.next).shouldBeSuccess()
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(
            toggle1.toDto(), toggle2.toDto(), toggle3.toDto()
        )

        httpClient(testerToken).listToggles(projectName2, null) shouldBeSuccess TogglesPageDto(
            items = listOf(toggle4.toDto()),
            next = null
        )
    }
    
    @Test
    fun `update toggle - toggle not found`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(adminToken).updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = oldNewData.toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: ${projectName1}/${toggleName1}"
        )
    }
    
    @Test
    fun `update toggle - project not found`() {
        httpClient(adminToken).updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = oldNewData.toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }
    
    @Test
    fun `update toggle - success`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val original = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        time += Duration.ofSeconds(5)

        val expected = original.copy(
            variations = onOffData.variations,
            defaultVariation = onOffData.defaultVariation,
            environments = onOffData.environments,
            updatedOn = time
        )

        httpClient(testerToken).updateToggle(
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
        httpClient(adminToken).getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `get toggle - toggle not found`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(adminToken).getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: ${projectName1}/${toggleName1}"
        )
    }

    @Test
    fun `get toggle - success`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        val toggle = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(testerToken).getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeSuccess toggle.toDto()
    }

    @Test
    fun `delete toggle - project not found`() {
        httpClient(adminToken).deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `delete toggle - toggle not found`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()

        httpClient(adminToken).deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: ${projectName1}/${toggleName1}"
        )
    }

    @Test
    fun `delete toggle - success as admin`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val toggle = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(adminToken).deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeSuccess toggle.toDto()

        toggles.listToggles(tenant1.tenantId, projectName1, null) shouldBeSuccess Page(
            items = emptyList(),
            next = null
        )
    }

    @Test
    fun `delete toggle - success as developer`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val toggle = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(developerToken).deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeSuccess toggle.toDto()
    }

    @Test
    fun `delete toggle - forbidden as tester`() {
        toggles.createProject(admin, ProjectCreateData(projectName1, devAndProd)).shouldBeSuccess()
        val toggle = toggles.createToggle(admin, projectName1, oldNewData.toCreate(toggleName1)).shouldBeSuccess()

        httpClient(testerToken).deleteToggle(
            projectName = toggle.projectName,
            toggleName = toggle.toggleName
        ) shouldBeFailure TogglesErrorDto("You must be an admin or developer to perform this action")
    }
}
