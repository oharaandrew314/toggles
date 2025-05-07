package dev.andrewohara.toggles

import dev.andrewohara.toggles.UniqueId.Companion.parse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.time.Instant
import java.util.Base64
import kotlin.random.Random

abstract class StorageContractBase {

    protected val random = Random(1337)
    protected var time: Instant = Instant.parse("2025-04-29T12:00:00Z")

    protected lateinit var storage: Storage
    abstract fun createStorage(): Storage

    @BeforeEach
    open fun setup() {
        storage = createStorage()
    }

    @AfterEach
    fun clear() = with(storage) {
        for (tenant in tenants.list(100)) {
            for (project in projects.list(tenant.tenantId, 100)) {
                for (key in apiKeys.list(tenant.tenantId, project.projectName, 100)) {
                    apiKeys -= key
                }
                for (toggle in toggles.list(tenant.tenantId, project.projectName, 100)) {
                    toggles -= toggle
                }

                projects -= project
            }

            tenants -= tenant
        }
    }

    private val base64 = Base64.getEncoder()
    fun nextUniqueId() = base64.encodeToString(random.nextBytes(UniqueId.LENGTH))
        .take(UniqueId.LENGTH)
        .let(::parse)
}