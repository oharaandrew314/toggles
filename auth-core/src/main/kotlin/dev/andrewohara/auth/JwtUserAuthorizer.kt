package dev.andrewohara.auth

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

fun UserAuthorizer.Companion.jwt(
    audience: List<String>,
    clock: Clock,
    issuer: String,
    algorithm: JWSAlgorithm = JWSAlgorithm.RS256,
    jwkSource: JWKSource<SecurityContext> // TODO replace with faked JWKS
): UserAuthorizer {
    val logger = KotlinLogging.logger { } // TODO maybe move the logging to a higher level

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
        runCatching { processor.process(idToken, null) }
            .onFailure { logger.debug("Failed to process JWT: $it") }
            .map { claims ->
                UserPrincipal(
                    name = claims.getStringClaim("name") ?: claims.getStringClaim("email"), // TODO gracefully handle null
                    emailAddress = EmailAddress.parse(claims.getStringClaim("email")), // TODO gracefully handle null
                    photoUrl = claims.getStringClaim("picture"),
                    expires = claims.expirationTime.toInstant()
                )
            }
            .getOrNull()
    }
}

fun UserAuthorizer.Companion.google(clientId: String, clock: Clock) = jwt(
    audience = listOf(clientId),
    clock = clock,
    issuer = "https://accounts.google.com",
    jwkSource = JWKSourceBuilder
        .create<SecurityContext>(URI.create("https://www.googleapis.com/oauth2/v3/certs").toURL())
        .build()
)