package dev.andrewohara.auth

fun interface UserAuthorizer {
    operator fun invoke(idToken: String): UserPrincipal?

    infix fun or(other: UserAuthorizer) =
        UserAuthorizer { invoke(it) ?: other(it) }

    companion object
}