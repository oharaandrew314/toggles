package dev.andrewohara.toggles.apikeys

import dev.forkhandles.values.AbstractValue
import dev.forkhandles.values.ValueFactory

class TokenMd5 private constructor(value: ByteArray): AbstractValue<ByteArray>(value) {
    @OptIn(ExperimentalStdlibApi::class)
    companion object: ValueFactory<TokenMd5, ByteArray>(
        coerceFn = ::TokenMd5,
        parseFn = String::hexToByteArray,
        showFn = ByteArray::toHexString,
        validation = { it.size == 16 }
    )

    override fun equals(other: Any?): Boolean {
        if (other !is TokenMd5) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode() = value.contentHashCode()
}