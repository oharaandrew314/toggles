package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.SubjectId
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.VariationName
import dev.andrewohara.toggles.Weight
import dev.andrewohara.toggles.source.ToggleSource
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
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

abstract class TogglesEngineContract(
    protected val projectName: ProjectName =  ProjectName.of("my_project")
) {
    private lateinit var engine: TogglesEngine

    abstract fun createSource(toggles: Map<ToggleName, ToggleState>): ToggleSource
    abstract fun setState(toggleName: ToggleName, state: ToggleState)

    @BeforeEach
    fun setup() {
        engine = TogglesEngine(
            projectName = projectName,
            defaultVariation = defaultVariation,
            toggleSource = createSource(mapOf(
                feature1 to ToggleState(
                    variations = mapOf(
                        off to Weight.of(2),
                        on to Weight.of(1)
                    ),
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
            ))
        )
    }


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

        // this isn't a great test; had to weight this heavily to get user2 to switch to on
        setState(feature1, ToggleState(
            variations = mapOf(
                off to Weight.of(1),
                on to Weight.of(100)
            ),
            defaultVariation = off,
            overrides = emptyMap()
        ))

        engine[feature1](user1) shouldBe on
        engine[feature1](user2) shouldBe on
    }
}