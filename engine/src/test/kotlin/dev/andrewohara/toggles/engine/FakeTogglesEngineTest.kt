package dev.andrewohara.toggles.engine

import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.ToggleState
import dev.andrewohara.toggles.source.ToggleSource
import dev.andrewohara.toggles.source.fake

class FakeTogglesEngineTest: TogglesEngineContract() {

    private val toggles = mutableMapOf<ToggleName, ToggleState>()

    override fun createSource(toggles: Map<ToggleName, ToggleState>): ToggleSource {
        this.toggles.clear()
        this.toggles += toggles

        return ToggleSource.fake(this.toggles)
    }

    override fun setState(toggleName: ToggleName, state: ToggleState) {
        toggles[toggleName] = state
    }
}