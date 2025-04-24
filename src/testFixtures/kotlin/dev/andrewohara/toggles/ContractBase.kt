package dev.andrewohara.toggles

import org.junit.jupiter.api.BeforeEach
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

abstract class ContractBase(val pageSize: Int = 2) {

    protected var time: Instant = Instant.parse("2025-04-24T12:00:00Z")
    protected val clock = object: Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: ZoneId?) = error("not implemented")
        override fun instant() = time
    }

    protected lateinit var toggles: Toggles
    abstract fun createToggles(clock: Clock, pageSize: Int): Toggles

    @BeforeEach
    fun setup() {
        toggles = createToggles(clock, pageSize)
    }
}