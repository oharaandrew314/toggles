package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName

data class ClientPrincipal(
    val projectName: ProjectName,
    val environment: EnvironmentName
)