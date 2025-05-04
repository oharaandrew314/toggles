package dev.andrewohara.toggles

import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.TokenMd5
import dev.andrewohara.toggles.storage.Storage
import dev.andrewohara.toggles.storage.getOrFail
import dev.andrewohara.utils.result.failIf
import dev.forkhandles.result4k.begin
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek
import java.security.MessageDigest
import java.time.Clock
import kotlin.random.Random

class Toggles(
    val storage: Storage,
    val pageSize: Int = 100,
    val clock: Clock = Clock.systemUTC(),
    val random: Random = Random.Default,
    private val secretKey: ByteArray
) {
    fun Toggles.hash(apiKey: ApiKey) = MessageDigest.getInstance("MD5").run {
        update(secretKey)
        val bytes = digest(apiKey.value.toByteArray())
        TokenMd5.of(bytes)
    }
}

// Projects

fun Toggles.createProject(data: ProjectCreateData) = begin
    .failIf({ storage.projects[data.projectName] != null}, { ProjectAlreadyExists(data.projectName)})
    .map {
        val time = clock.instant()
        Project(data.projectName, time, time, data.environments)
    }
    .peek(storage.projects::plusAssign)

fun Toggles.updateProject(projectName: ProjectName, data: ProjectUpdateData) = storage
    .projects.getOrFail(projectName)
    .map { it.copy(environments = data.environments, updatedOn = clock.instant()) }
    .peek(storage.projects::plusAssign)

fun Toggles.listProjects(cursor: ProjectName?) =
    storage.projects.list(pageSize)[cursor]

fun Toggles.deleteProject(projectName: ProjectName) = storage
    .projects.getOrFail(projectName)
    .failIf({storage.toggles.list(projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .failIf({storage.apiKeys.list(projectName, pageSize).any()}, {ProjectNotEmpty(projectName)})
    .peek { storage.projects.minusAssign(projectName) }

// Toggles

fun Toggles.listToggles(projectName: ProjectName, cursor: ToggleName?) = storage
    .projects.getOrFail(projectName)
    .map { storage.toggles.list(projectName, pageSize)[cursor] }

fun Toggles.getToggle(projectName: ProjectName, toggleName: ToggleName) = storage
    .projects.getOrFail(projectName)
    .flatMap { storage.toggles.getOrFail(projectName, toggleName) }

fun Toggles.createToggle(projectName: ProjectName, data: ToggleCreateData) = storage
    .projects.getOrFail(projectName)
    .failIf({ storage.toggles[projectName, data.toggleName] != null}, { ToggleAlreadyExists(projectName, data.toggleName) })
    .map { data.toToggle(projectName, clock.instant(), random) }
    .peek(storage.toggles::plusAssign)

fun Toggles.updateToggle(projectName: ProjectName, toggleName: ToggleName, data: ToggleUpdateData) = storage
    .projects.getOrFail(projectName)
    .flatMap { storage.toggles.getOrFail(projectName, toggleName) }
    .map { it.update(data, clock.instant()) }
    .peek(storage.toggles::plusAssign)

fun Toggles.deleteToggle(projectName: ProjectName, toggleName: ToggleName) = storage
    .projects.getOrFail(projectName)
    .flatMap { storage.toggles.getOrFail(projectName, toggleName) }
    .peek { storage.toggles.remove(projectName, toggleName) }

// API Keys

fun Toggles.generateApiKey(projectName: ProjectName, environmentName: EnvironmentName) = storage
    .projects.getOrFail(projectName)
    .map { ApiKeyMeta(projectName, environmentName, clock.instant()) to ApiKey.random(random) }
    .peek { (meta, key) -> storage.apiKeys[meta] = hash(key) }



