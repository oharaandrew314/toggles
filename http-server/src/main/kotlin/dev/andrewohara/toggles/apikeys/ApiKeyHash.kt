package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.HexValue
import dev.andrewohara.toggles.HexValueFactory

class ApiKeyHash private constructor(value: ByteArray): HexValue(value) {
    companion object: HexValueFactory<ApiKeyHash>(::ApiKeyHash, 32)
}