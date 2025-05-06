package dev.andrewohara.toggles

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import dev.andrewohara.toggles.http.client.TogglesHttpClient
import dev.andrewohara.toggles.http.server.toHttpServer
import dev.andrewohara.toggles.users.UserAuthorizer
import dev.andrewohara.toggles.users.jwt
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.junit.jupiter.api.BeforeEach
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import java.time.Clock
import java.time.ZoneId
import java.time.ZoneOffset

private const val AUDIENCE = "toggles-test"

abstract class ServerContractBase(val pageSize: Int = 2): StorageContractBase() {

    private val keyPair = KeyPairGenerator.getInstance("RSA")
        .apply { initialize(2048) }
        .generateKeyPair()

    fun createToken(
        emailAddress: EmailAddress,
        issuer: String = emailAddress.value.split("@").last()
    ): String {
        val header = JWSHeader.Builder(JWSAlgorithm.RS256).build()
        val claims = JWTClaimsSet.Builder()
            .audience(AUDIENCE)
            .issuer(issuer)
            .claim("email", emailAddress.value)
            .build()

        return SignedJWT(header, claims)
            .apply { sign(RSASSASigner(keyPair.private)) }
            .serialize()
    }

    fun httpClient(idToken: String) = TogglesHttpClient(
        host = Uri.of(""),
        idToken = idToken,
        internet = httpServer
    )

    val clock = object: Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: ZoneId?) = error("not implemented")
        override fun instant() = time
    }

    lateinit var toggles: TogglesApp
    lateinit var httpServer: HttpHandler

    @BeforeEach
    override fun setup() {
        super.setup()
        toggles = TogglesApp(
            storage = storage,
            random = random,
            pageSize = pageSize,
            clock = clock,
            secretKey = random.nextBytes(16),
            userAuthorizer = UserAuthorizer.jwt(
                audience = listOf(AUDIENCE),
                issuer = IDP1,
                clock = clock,
                // TODO override JWKS URI instead of source
                jwkSource = ImmutableJWKSet(JWKSet(
                    RSAKey.Builder(keyPair.public as RSAPublicKey)
                        .algorithm(JWSAlgorithm.RS256)
                        .build()
                ))
            )
        )

        httpServer = toggles.toHttpServer()
    }
}