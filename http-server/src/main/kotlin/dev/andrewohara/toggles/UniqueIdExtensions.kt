package dev.andrewohara.toggles

import kotlin.random.Random

private val BASE64_CHARS = ('A'..'Z') + ('a'..'z') + ('0'..'9')

fun UniqueId.Companion.random(random: Random)= (1..UniqueId.LENGTH)
    .map { BASE64_CHARS.random(random) }
    .joinToString("")
    .let(UniqueId::parse)
