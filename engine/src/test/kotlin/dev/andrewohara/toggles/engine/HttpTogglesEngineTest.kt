package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.InMemoryTogglesSource
import dev.andrewohara.toggles.ProjectData
import dev.andrewohara.toggles.ToggleCreateData
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.Toggles
import dev.andrewohara.toggles.createProject
import dev.andrewohara.toggles.createToggle
import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.toToggleSource
import dev.andrewohara.toggles.updateToggle
import dev.forkhandles.result4k.kotest.shouldBeSuccess

class HttpTogglesEngineTest: TogglesEngineContract(), InMemoryTogglesSource {

    private lateinit var service: Toggles

    override fun createSource(toggles: Map<ToggleName, ToggleState>): ToggleSource {
        service = createToggles()

        service.createProject(ProjectData(projectName = projectName)).shouldBeSuccess()
        for ((toggleName, toggleState) in toggles) {
            val data = ToggleCreateData(
                toggleName = toggleName,
                variations = toggleState.variations,
                overrides = toggleState.overrides,
                defaultVariation = toggleState.defaultVariation
            )

            service.createToggle(projectName = projectName, data = data).shouldBeSuccess()
        }

        return service.toToggleSource()
    }

    override fun setState(toggleName: ToggleName, state: ToggleState) {
        service.updateToggle(projectName, toggleName, state).shouldBeSuccess()
    }
}