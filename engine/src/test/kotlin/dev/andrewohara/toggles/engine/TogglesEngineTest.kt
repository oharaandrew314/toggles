package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.source.fake
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

private val feature1 = ToggleName.of("feature_1")
private val feature2 = ToggleName.of("feature_2")

private val user1 = SubjectId.of("user1")
private val user2 = SubjectId.of("user-second")
private val testUser = SubjectId.of("testuser")


private val off = VariationName.of("off")
private val on = VariationName.of("on")
private val enabled = VariationName.of("enabled")
private val disabled = VariationName.of("disabled")

private val defaultVariation = VariationName.of("default")

class TogglesEngineTest {
    private val feature1Variations = mutableMapOf(
        off to Weight.of(2),
        on to Weight.of(1)
    )

    private val engine = TogglesEngine(
        projectName = ProjectName.of("my_project"),
        defaultVariation = defaultVariation,
        toggleSource = ToggleSource.fake(
            toggles = mapOf(
                feature1 to ToggleState(
                    variations = feature1Variations,
                    defaultVariation = off,
                    overrides = mapOf(
                        testUser to on
                    )
                ),
                feature2 to ToggleState(
                    variations = mapOf(
                        disabled to Weight.of(0),
                        enabled to Weight.of(1)
                    ),
                    defaultVariation = disabled,
                    overrides = emptyMap()
                )
            )
        )
    )

    @Test
    fun `get missing feature`() {
        engine[ToggleName.of("missing_feature")](user1) shouldBe defaultVariation
    }

    @Test
    fun `get naturally off`() {
        engine[feature1](user1) shouldBe off
    }

    @Test
    fun `get naturally on`() {
        engine[feature1](user2) shouldBe on
    }

    @Test
    fun `get overridden`() {
        engine[feature1](testUser) shouldBe on
    }

    @Test
    fun `get always enabled`() {
        engine[feature2](user1) shouldBe enabled
        engine[feature2](user2) shouldBe enabled
        engine[feature2](testUser) shouldBe enabled
    }

    @Test
    fun `variation is sticky`() {
        engine[feature1](user1) shouldBe off
        engine[feature1](user2) shouldBe on

        feature1Variations[off] = Weight.of(1)

        // this isn't a great test; had to weight this heavily to get user2 to switch to on
        feature1Variations[on] = Weight.of(100)

        engine[feature1](user1) shouldBe on
        engine[feature1](user2) shouldBe on
    }
}