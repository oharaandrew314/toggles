package dev.andrewohara.toggles.apikeys

import dev.forkhandles.values.AbstractValue
import dev.forkhandles.values.ValueFactory

class TokenSha256 private constructor(value: ByteArray): AbstractValue<ByteArray>(value) {
    @OptIn(ExperimentalStdlibApi::class)
    companion object: ValueFactory<TokenSha256, ByteArray>(
        coerceFn = ::TokenSha256,
        parseFn = String::hexToByteArray,
        showFn = ByteArray::toHexString,
        validation = { it.size == 32 }
    )

    override fun equals(other: Any?): Boolean {
        if (other !is TokenSha256) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode() = value.contentHashCode()
}