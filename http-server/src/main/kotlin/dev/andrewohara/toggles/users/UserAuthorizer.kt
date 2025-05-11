package dev.andrewohara.toggles.users

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import dev.andrewohara.toggles.EmailAddress
import mu.KotlinLogging
import java.net.URI
import java.time.Clock
import java.util.Date

fun interface UserAuthorizer {
    operator fun invoke(idToken: String): EmailAddress?

    companion object
}

private val googleJwkUri = URI.create("https://www.googleapis.com/oauth2/v3/certs").toURL()
private const val googleIss = "accounts.google.com"

fun UserAuthorizer.Companion.jwt(
    audience: List<String>,
    clock: Clock,
    issuer: String = googleIss, // TODO support multiple issuers
    algorithm: JWSAlgorithm = JWSAlgorithm.RS256,
    jwkSource: JWKSource<SecurityContext> = JWKSourceBuilder
        .create<SecurityContext>(googleJwkUri)
        .build()
): UserAuthorizer {
    val logger = KotlinLogging.logger { }

    val processor = DefaultJWTProcessor<SecurityContext>().apply {
        jwtClaimsSetVerifier = object: DefaultJWTClaimsVerifier<SecurityContext>(
            JWTClaimsSet.Builder()
                .issuer(issuer)
                .audience(audience)
                .build(),
            emptySet()
        ) {
            override fun currentTime() = Date.from(clock.instant())
        }
        jwsKeySelector = JWSVerificationKeySelector(algorithm, jwkSource)
    }

    return UserAuthorizer { idToken ->
        kotlin
            .runCatching { processor.process(idToken, null) }
            .onFailure { logger.debug("Failed to process JWT: $it") }
            .map { EmailAddress.parse(it.getStringClaim("email")) }
            .getOrNull()
    }
}