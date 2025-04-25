package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.TogglesErrorDto
import dev.andrewohara.toggles.http.TogglesPageDto
import dev.andrewohara.toggles.http.server.toDto
import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.junit.jupiter.api.Test
import java.time.Duration

abstract class TogglesHttpContract: ContractBase() {
    
    @Test
    fun `create toggle - project not found`() {
        httpClient.createToggle(
            projectName = projectName1,
            data = toggleData1.toCreate(toggleName1).toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }
    
    @Test
    fun `create toggle - already exists`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()

        httpClient.createToggle(
            projectName = projectName1,
            data = toggleData1.toCreate(toggleName1).toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle already exists: $projectName1/$toggleName1"
        )
    }
    
    @Test
    fun `create toggle - success`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()

        val expected = Toggle(
            projectName = projectName1,
            toggleName = toggleName1,
            variations = toggleData1.variations,
            defaultVariation = toggleData1.defaultVariation,
            overrides = toggleData1.overrides,
            createdOn = time,
            updatedOn = time
        )

        httpClient.createToggle(
            projectName = projectName1,
            data = toggleData1.toCreate(toggleName1).toDto()
        ) shouldBeSuccess expected.toDto()

        toggles.listToggles(projectName1, null) shouldBeSuccess Page(
            items = listOf(expected),
            next = null
        )
    }

    @Test
    fun `create toggle - duplicate in other project`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        toggles.createProject(ProjectData(projectName2)).shouldBeSuccess()
        val toggle1 = toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()

        val toggle2 = httpClient.createToggle(
            projectName = projectName2,
            data = toggleData1.toCreate(toggleName1).toDto()
        ).shouldBeSuccess()

        toggles.listToggles(projectName1, null) shouldBeSuccess Page(listOf(toggle1), null)
        httpClient.listToggles(projectName2, null) shouldBeSuccess TogglesPageDto(listOf(toggle2), null)
    }
    
    @Test
    fun `list toggles - success`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        toggles.createProject(ProjectData(projectName2)).shouldBeSuccess()

        val toggle1 = toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()
        val toggle2 = toggles.createToggle(projectName1, toggleData1.toCreate(toggleName2)).shouldBeSuccess()
        val toggle3 = toggles.createToggle(projectName1, toggleData1.toCreate(toggleName3)).shouldBeSuccess()
        val toggle4 = toggles.createToggle(projectName2, toggleData1.toCreate(toggleName1)).shouldBeSuccess()

        httpClient.listToggles(projectName1, null) shouldBeSuccess TogglesPageDto(
            items = listOf(toggle1.toDto(), toggle2.toDto()),
            next = toggle2.toggleName
        )

        httpClient.listToggles(projectName1, toggle2.toggleName) shouldBeSuccess TogglesPageDto(
            items = listOf(toggle3.toDto()),
            next = null
        )

        httpClient.listToggles(projectName2, null) shouldBeSuccess TogglesPageDto(
            items = listOf(toggle4.toDto()),
            next = null
        )
    }
    
    @Test
    fun `update toggle - toggle not found`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()

        httpClient.updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = toggleData1.toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: $projectName1/$toggleName1"
        )
    }
    
    @Test
    fun `update toggle - project not found`() {
        httpClient.updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = toggleData1.toDto()
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }
    
    @Test
    fun `update toggle - success`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        val original = toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()

        time += Duration.ofSeconds(5)

        val expected = original.copy(
            variations = toggleData2.variations,
            defaultVariation = toggleData2.defaultVariation,
            overrides = toggleData2.overrides,
            updatedOn = time
        )

        httpClient.updateToggle(
            projectName = projectName1,
            toggleName = toggleName1,
            data = toggleData2.toDto()
        ) shouldBeSuccess expected.toDto()

        toggles.listToggles(projectName1, null) shouldBeSuccess Page(
            items = listOf(expected),
            next = null
        )
    }
    
    @Test
    fun `get toggle - project not found`() {
        httpClient.getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `get toggle - toggle not found`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()

        httpClient.getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: $projectName1/$toggleName1"
        )
    }

    @Test
    fun `get toggle - success`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()

        val toggle = toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()

        httpClient.getToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeSuccess toggle.toDto()
    }

    @Test
    fun `delete toggle - project not found`() {
        httpClient.deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Project not found: $projectName1"
        )
    }

    @Test
    fun `delete toggle - toggle not found`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()

        httpClient.deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeFailure TogglesErrorDto(
            message = "Toggle not found: $projectName1/$toggleName1"
        )
    }

    @Test
    fun `delete toggle - success`() {
        toggles.createProject(ProjectData(projectName1)).shouldBeSuccess()
        val toggle = toggles.createToggle(projectName1, toggleData1.toCreate(toggleName1)).shouldBeSuccess()

        httpClient.deleteToggle(
            projectName = projectName1,
            toggleName = toggleName1
        ) shouldBeSuccess toggle.toDto()

        toggles.listToggles(projectName1, null) shouldBeSuccess Page(
            items = emptyList(),
            next = null
        )
    }
}