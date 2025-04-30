package dev.andrewohara.toggles

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class ToggleStorageContract: StorageContractBase() {

    private lateinit var toggle1: Toggle
    private lateinit var toggle2: Toggle
    private lateinit var toggle3: Toggle
    private lateinit var toggle4: Toggle

    @BeforeEach
    override fun setup() {
        super.setup()

        toggle1 = mostlyOld
            .toCreate(toggleName1)
            .toToggle(projectName1, t0)
            .also(storage::upsertToggle)

        toggle2 = alwaysOn
            .toCreate(toggleName2)
            .toToggle(projectName1, t0.plusSeconds(60))
            .also(storage::upsertToggle)

        toggle3 = mostlyOld
            .toCreate(toggleName3)
            .toToggle(projectName1, t0.plusSeconds(120))
            .also(storage::upsertToggle)

        toggle4 = alwaysOn
            .toCreate(toggleName1)
            .toToggle(projectName2, t0)
            .also(storage::upsertToggle)
    }

    @Test
    fun `list toggles - paged`() {
        val page1 = storage.listToggles(projectName1, pageSize = 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.listToggles(projectName1, pageSize = 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(toggle1, toggle2, toggle3)
    }

    @Test
    fun `get toggle - found`() {
        storage.getToggle(projectName2, toggleName1) shouldBe toggle4
    }

    @Test
    fun `get toggle - not found`() {
        storage.getToggle(projectName2, toggleName2).shouldBeNull()
    }

    @Test
    fun `delete toggle - found`() {
        storage.deleteToggle(projectName1, toggleName1)

        storage.listToggles(projectName1, 100)
            .toList()
            .shouldContainExactlyInAnyOrder(toggle2, toggle3)
    }

    @Test
    fun `delete toggle - not found`() {
        storage.deleteToggle(projectName2, toggleName2)
    }

    @Test
    fun `save - can update`() {
        val updated = toggle1.copy(
            defaultVariation = new
        )

        storage.upsertToggle(updated)
        storage.getToggle(toggle1.projectName, toggle1.toggleName) shouldBe updated
    }
}