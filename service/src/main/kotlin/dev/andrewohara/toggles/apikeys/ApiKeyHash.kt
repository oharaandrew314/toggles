package dev.andrewohara.toggles.apikeys

import dev.forkhandles.values.Base16StringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.exactLength

class ApiKeyHash private constructor(value: String): StringValue(value) {
    companion object: Base16StringValueFactory<ApiKeyHash>(
        fn = ::ApiKeyHash,
        validation = 64.exactLength,
        parseFn = String::uppercase,
        showFn = String::lowercase
    )
}