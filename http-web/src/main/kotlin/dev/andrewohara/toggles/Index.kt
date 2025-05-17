package dev.andrewohara.toggles

import dev.andrewohara.auth.UserPrincipal
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import org.http4k.core.Uri

data class GoogleSignInConfig(
    val clientId: String,
    val redirectUri: Uri
)

fun TogglesWeb.index(
    principal: UserPrincipal?
) = createHTML().html {
    head {
        title("Toggles")
    }
    body {
        h1 {
            +"Toggles"
        }

        if (principal != null) {
            + "${principal.name}(${principal.emailAddress})"
            logout()
        } else {
            googleSignIn(googleSignInConfig)
        }

    }
}

private fun FlowContent.googleSignIn(config: GoogleSignInConfig) {
    script {
        src = "https://accounts.google.com/gsi/client"
        async = true
    }
    div {
        id = "g_id_onload"
        attributes["data-client_id"] = config.clientId
        attributes["data-login_uri"] = config.redirectUri.toString()
        attributes["data-ux_mode"] = "redirect"
        attributes["data-auto_prompt"] = "false"
    }
    div {
        classes = setOf("g_id_signin")
        attributes["data-type"] = "standard"
        attributes["data-size"] = "large"
        attributes["data-theme"] = "outline"
        attributes["data-text"] = "sign_in_with"
        attributes["data-shape"] = "rectangular"
        attributes["data-logo_alignment"] = "left"
    }
}

private fun FlowContent.logout() {
    form {
        method = FormMethod.post
        action = "logout"

        button {
            type = ButtonType.submit
            +"Logout"
        }
    }
}