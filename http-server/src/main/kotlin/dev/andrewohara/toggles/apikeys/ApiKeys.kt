package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.ApiKey

fun interface ApiKeys {
    fun exchange(apiKey: ApiKey): ClientPrincipal?
}