package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.ToggleStorage
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

abstract class ToggleStorageContract {

    private val t0 = Instant.parse("2025-04-29T12:00:00Z")

    private lateinit var toggleStorage: ToggleStorage
    abstract fun createStorage(): ToggleStorage

    private lateinit var toggle1: Toggle
    private lateinit var toggle2: Toggle
    private lateinit var toggle3: Toggle
    private lateinit var toggle4: Toggle

    @BeforeEach
    fun setup() {
        toggleStorage = createStorage()

        toggle1 = mostlyOld
            .toCreate(toggleName1)
            .toToggle(projectName1, t0)
            .also(toggleStorage::plusAssign)

        toggle2 = alwaysOn
            .toCreate(toggleName2)
            .toToggle(projectName1, t0.plusSeconds(60))
            .also(toggleStorage::plusAssign)

        toggle3 = mostlyOld
            .toCreate(toggleName3)
            .toToggle(projectName1, t0.plusSeconds(120))
            .also(toggleStorage::plusAssign)

        toggle4 = alwaysOn
            .toCreate(toggleName1)
            .toToggle(projectName2, t0)
            .also(toggleStorage::plusAssign)
    }

    @Test
    fun `list toggles - paged`() {
        val page1 = toggleStorage.list(projectName1, pageSize = 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = toggleStorage.list(projectName1, pageSize = 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()

        page1.items.plus(page2.items).shouldContainExactlyInAnyOrder(toggle1, toggle2, toggle3)
    }

    @Test
    fun `get toggle - found`() {
        toggleStorage[projectName2, toggleName1] shouldBe toggle4
    }

    @Test
    fun `get toggle - not found`() {
        toggleStorage[projectName2, toggleName2].shouldBeNull()
    }

    @Test
    fun `delete toggle - found`() {
        toggleStorage.delete(projectName1, toggleName1) shouldBe toggle1

        toggleStorage.list(projectName1, 100)
            .toList()
            .shouldContainExactlyInAnyOrder(toggle2, toggle3)
    }

    @Test
    fun `delete toggle - not found`() {
        toggleStorage.delete(projectName2, toggleName2).shouldBeNull()
    }
}