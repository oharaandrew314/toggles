package dev.andrewohara.toggles

import dev.andrewohara.toggles.repo.ProjectRepo
import dev.andrewohara.toggles.repo.TogglesRepo
import dev.andrewohara.toggles.repo.inMemory
import java.time.Clock

class InMemoryTogglesTest: TogglesContract() {

    override fun createToggles(clock: Clock, pageSize: Int) = Toggles(
        clock = clock,
        pageSize  = pageSize,
        projects = ProjectRepo.Companion.inMemory(),
        toggles = TogglesRepo.Companion.inMemory()
    )
}