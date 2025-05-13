package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.TogglesApp
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.UniqueId.Companion.parse
import java.security.MessageDigest
import java.util.Base64

private val base64 = Base64.getEncoder()

fun TogglesApp.createUniqueId(vararg seeds: Any) = MessageDigest.getInstance("MD5").run {
    update(secretKey)
    for (seed in seeds) { update(seed.toString().toByteArray())}
    parse(base64.encodeToString(digest()).take(UniqueId.LENGTH))
}