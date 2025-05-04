package dev.andrewohara.toggles

import dev.andrewohara.toggles.apikeys.TokenMd5
import java.security.MessageDigest

class Crypt(
    val secretKey: ByteArray
)

fun Crypt.hash(apiKey: ApiKey) = MessageDigest.getInstance("SHA-256").run {
    update(secretKey)
    val bytes = digest(apiKey.value.toByteArray())
    TokenMd5.of(bytes)
}