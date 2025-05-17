package dev.andrewohara.toggles

import dev.andrewohara.auth.UserAuthorizer
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.hash
import dev.andrewohara.toggles.engine.engineApiV1
import dev.andrewohara.toggles.projects.projectApiV1
import dev.andrewohara.toggles.toggles.toggleApiV1
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.userApiV1
import dev.forkhandles.values.parseOrNull
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.HttpHandler
import org.http4k.format.Moshi
import org.http4k.lens.RequestKey
import org.http4k.routing.routes
import org.http4k.security.BearerAuthSecurity

fun TogglesApp.toHttpServer(userAuthorizer: UserAuthorizer): HttpHandler {
    val clientAuthLens = RequestKey.required<ApiKeyMeta>("client_auth")
    val clientSecurity = BearerAuthSecurity(clientAuthLens, lookup = { token ->
        ApiKey.parseOrNull(token)
            ?.let { hash(it) }
            ?.let(storage.apiKeys::get)
    })

    val userAuthLens = RequestKey.required<User>("user_auth")
    val userSecurity = BearerAuthSecurity(userAuthLens, lookup = { idToken ->
        userAuthorizer(idToken)?.let { storage.users[it.emailAddress] }
    })

    val service = this

    val api = contract {
        renderer = OpenApi3(
            ApiInfo("Toggles API", "1"),
            apiRenderer = OpenApi3ApiRenderer(Moshi),
            json = Moshi
        )
        descriptionPath = "openapi.json"

        routes += projectApiV1(service, userSecurity, userAuthLens)
        routes += toggleApiV1(service, userSecurity, userAuthLens)
        routes += engineApiV1(service, clientSecurity, clientAuthLens)
        routes += userApiV1(service, userSecurity, userAuthLens)
    }

    val ui = swaggerUiLite {
        pageTitle = "Toggles API"
        url = "openapi.json"

        displayOperationId = true
        displayRequestDuration = true
        requestSnippetsEnabled = true
        tryItOutEnabled = true
        deepLinking = true
    }

    return routes(api, ui)
}