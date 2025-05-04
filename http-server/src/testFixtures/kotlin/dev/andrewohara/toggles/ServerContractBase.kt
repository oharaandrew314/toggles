package dev.andrewohara.toggles

import dev.andrewohara.toggles.http.client.TogglesHttpClient
import dev.andrewohara.toggles.http.server.toHttpServer
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.junit.jupiter.api.BeforeEach
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random

abstract class ServerContractBase(val pageSize: Int = 2): StorageContractBase() {

    protected var time: Instant = Instant.parse("2025-04-24T12:00:00Z")
    protected val clock = object: Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: ZoneId?) = error("not implemented")
        override fun instant() = time
    }

    protected lateinit var toggles: Toggles
    protected lateinit var httpServer: HttpHandler
    protected lateinit var httpClient: TogglesHttpClient

    @BeforeEach
    override fun setup() {
        super.setup()

        val random = Random(1337)
        toggles = Toggles(
            storage = storage,
            random = random,
            pageSize = pageSize,
            clock = clock,
            secretKey = random.nextBytes(16)
        )
        httpServer = toggles.toHttpServer()
        httpClient = TogglesHttpClient(Uri.Companion.of(""), httpServer)
    }
}