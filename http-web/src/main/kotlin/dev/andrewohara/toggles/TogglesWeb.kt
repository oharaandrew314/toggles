package dev.andrewohara.toggles

import dev.andrewohara.auth.UserAuthorizer
import dev.andrewohara.auth.UserPrincipal
import org.http4k.core.ContentType
import org.http4k.core.Filter
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.invalidateCookie
import org.http4k.core.with
import org.http4k.lens.RequestKey
import org.http4k.lens.contentType
import org.http4k.lens.location
import org.http4k.routing.bind
import org.http4k.routing.routes

data class TogglesWeb(
    val service: TogglesApp,
    val googleSignInConfig: GoogleSignInConfig,
    val secureCookies: Boolean,
    val userAuth: UserAuthorizer
)

private const val TOKEN_COOKIE_NAME = "token"

private val principalLens = RequestKey.optional<UserPrincipal>("principal")
private val toIndex =  Response(Status.SEE_OTHER).location(Uri.of("/"))

fun TogglesWeb.toHttp() = routes(
    "/" bind Method.GET to {
        Response(Status.OK)
            .contentType(ContentType.TEXT_HTML)
            .body(index( principal(it)))
    },
    "logout" bind Method.POST to {
        toIndex.invalidateCookie(TOKEN_COOKIE_NAME, path = "/")
    },
    "redirect" bind Method.POST to fn@{ request ->

        // verify id token
        val idToken = request.form("credential") ?: return@fn Response(Status.BAD_REQUEST)
        val principal = userAuth(idToken) ?: return@fn Response(Status.UNAUTHORIZED)

        toIndex.cookie(Cookie(
            name = TOKEN_COOKIE_NAME,
            value = idToken, // TODO encrypt
            secure = secureCookies,
            sameSite = SameSite.Lax,
            httpOnly = true,
            expires = principal.expires
        ))
    }
)

private fun TogglesWeb.principal(request: Request) = request
    .cookie(TOKEN_COOKIE_NAME)?.value
    ?.let(userAuth::invoke)


private fun TogglesWeb.webAuth() = Filter { next ->
    fn@{ request ->
        val principal = principal(request) ?: return@fn toIndex
        next(request.with(principalLens of principal))
    }
}