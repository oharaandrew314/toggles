package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.source.fake
import dev.andrewohara.toggles.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TogglesEngineTest {

    private val engine = TogglesEngine(
        defaultVariation = off,
        toggleSource = ToggleSource.fake(mapOf(
            toggleName1 to ToggleState(
                uniqueId = uid1,
                variations = mapOf(
                    off to Weight.of(2),
                    on to Weight.of(1)
                ),
                defaultVariation = off,
                overrides = mapOf(
                    testUser to on
                )
            ),
            toggleName2 to ToggleState(
                uniqueId = uid2,
                variations = emptyMap(), // important to keep empty
                defaultVariation = on,
                overrides = emptyMap()
            )
        ))
    )

    @Test
    fun `get missing feature`() {
        engine[toggleName3](user1) shouldBe off
    }

    @Test
    fun `get naturally off`() {
        engine[toggleName1](user1) shouldBe off
    }

    @Test
    fun `get naturally on`() {
        engine[toggleName1](user2) shouldBe on
    }

    @Test
    fun `get overridden`() {
        engine[toggleName1](testUser) shouldBe on
    }

    @Test
    fun `get always enabled - ie no variations`() {
        engine[toggleName2](user1) shouldBe on
        engine[toggleName2](user2) shouldBe on
        engine[toggleName2](testUser) shouldBe on
    }
}