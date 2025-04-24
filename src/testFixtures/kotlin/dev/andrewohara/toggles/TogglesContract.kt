package dev.andrewohara.toggles

import dev.andrewohara.utils.pagination.Page
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.junit.jupiter.api.Test
import java.time.Duration

abstract class TogglesContract: ContractBase() {
    
    @Test
    fun `create toggle - project not found`() {
        toggles.createToggle(projectName1, toggleName1, toggleData1) shouldBeFailure ProjectNotFound(projectName1)
    }
    
    @Test
    fun `create toggle - already exists`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()
        toggles.createToggle(projectName1, toggleName1, toggleData1) shouldBeFailure ToggleAlreadyExists(
            projectName1,
            toggleName1
        )
    }
    
    @Test
    fun `create toggle - success`() {
        toggles.createProject(projectName1).shouldBeSuccess()

        val expected = Toggle(
            projectName = projectName1,
            toggleName = toggleName1,
            variations = toggleData1.variations,
            defaultVariation = toggleData1.defaultVariation,
            overrides = toggleData1.overrides,
            createdOn = time,
            updatedOn = time
        )

        toggles.createToggle(projectName1, toggleName1, toggleData1) shouldBeSuccess expected
        toggles.listToggles(projectName1, null) shouldBeSuccess Page(
            items = listOf(expected),
            next = null
        )
    }

    @Test
    fun `create toggle - duplicate in other project`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.createProject(projectName2).shouldBeSuccess()

        val toggle1 = toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()
        val toggle2 = toggles.createToggle(projectName2, toggleName1, toggleData2).shouldBeSuccess()

        toggles.listToggles(projectName1, null) shouldBeSuccess Page(listOf(toggle1), null)
        toggles.listToggles(projectName2, null) shouldBeSuccess Page(listOf(toggle2), null)
    }
    
    @Test
    fun `list toggles - success`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.createProject(projectName2).shouldBeSuccess()

        val toggle1 = toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()
        val toggle2 = toggles.createToggle(projectName1, toggleName2, toggleData1).shouldBeSuccess()
        val toggle3 = toggles.createToggle(projectName1, toggleName3, toggleData1).shouldBeSuccess()
        val toggle4 = toggles.createToggle(projectName2, toggleName1, toggleData1).shouldBeSuccess()

        toggles.listToggles(projectName1, null) shouldBeSuccess Page(
            items = listOf(toggle1, toggle2),
            next = toggle2.toggleName
        )

        toggles.listToggles(projectName1, toggle2.toggleName) shouldBeSuccess Page(
            items = listOf(toggle3),
            next = null
        )

        toggles.listToggles(projectName2, null) shouldBeSuccess Page(
            items = listOf(toggle4),
            next = null
        )
    }
    
    @Test
    fun `update toggle - toggle not found`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.updateToggle(projectName1, toggleName1, toggleData1) shouldBeFailure ToggleNotFound(
            projectName1,
            toggleName1
        )
    }
    
    @Test
    fun `update toggle - project not found`() {
        toggles.updateToggle(projectName1, toggleName1, toggleData1) shouldBeFailure ProjectNotFound(projectName1)
    }
    
    @Test
    fun `update toggle - success`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        val original = toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()

        time += Duration.ofSeconds(5)

        val expected = original.copy(
            variations = toggleData2.variations,
            defaultVariation = toggleData2.defaultVariation,
            overrides = toggleData2.overrides,
            updatedOn = time
        )

        toggles.updateToggle(projectName1,  toggleName1, toggleData2) shouldBeSuccess expected
        toggles.listToggles(projectName1, null) shouldBeSuccess Page(
            items = listOf(expected),
            next = null
        )
    }
    
    @Test
    fun `get toggle - project not found`() {
        toggles.getToggle(projectName1, toggleName1) shouldBeFailure ProjectNotFound(projectName1)
    }

    @Test
    fun `get toggle - toggle not found`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.getToggle(projectName1, toggleName1) shouldBeFailure ToggleNotFound(projectName1, toggleName1)
    }

    @Test
    fun `get toggle - success`() {
        toggles.createProject(projectName1).shouldBeSuccess()

        val toggle = toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()
        toggles.getToggle(projectName1, toggleName1) shouldBeSuccess toggle
    }

    @Test
    fun `delete toggle - project not found`() {
        toggles.deleteToggle(projectName1, toggleName1) shouldBeFailure ProjectNotFound(projectName1)
    }

    @Test
    fun `delete toggle - toggle not found`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        toggles.deleteToggle(projectName1, toggleName1) shouldBeFailure ToggleNotFound(projectName1, toggleName1)
    }

    @Test
    fun `delete toggle - success`() {
        toggles.createProject(projectName1).shouldBeSuccess()
        val toggle = toggles.createToggle(projectName1, toggleName1, toggleData1).shouldBeSuccess()

        toggles.deleteToggle(projectName1, toggleName1) shouldBeSuccess toggle
        toggles.listToggles(projectName1, null) shouldBeSuccess Page(
            items = emptyList(),
            next = null
        )
    }
}