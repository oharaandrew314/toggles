package dev.andrewohara.toggles

import dev.andrewohara.auth.UserAuthorizer
import dev.andrewohara.auth.google
import dev.andrewohara.togles.storage.dynamoDb
import dev.andrewohara.utils.http4k.logSummary
import org.http4k.base64DecodedArray
import org.http4k.client.JavaHttpClient
import org.http4k.config.Environment
import org.http4k.connect.amazon.CredentialsChain
import org.http4k.connect.amazon.Environment
import org.http4k.connect.amazon.Profile
import org.http4k.connect.amazon.RegionProvider
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.core.HttpHandler
import org.http4k.connect.amazon.sts.StsProfile
import org.http4k.core.then
import org.http4k.filter.ResponseFilters
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import java.time.Clock
import kotlin.random.Random

fun main() {
    ResponseFilters.logSummary()
        .then(createWeb().toHttp())
        .asServer(SunHttp(Environment.ENV[Config.debugPort]!!.value))
        .start()
        .also { println("Started web on http://localhost:${it.port()}") }
}

fun createWeb(
    env: Environment = Environment.ENV,
    internet: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    random: Random = Random.Default,
) = TogglesWeb(
    service = createService(
        clock = clock,
        env = env,
        internet = internet,
        random = random
    ),
    secureCookies = env[Config.debugPort] == null,
    userAuth = UserAuthorizer.google(
        clientId = env[Config.googleClientId],
        clock = clock
    ),
    googleSignInConfig = GoogleSignInConfig(
        clientId = env[Config.googleClientId],
        redirectUri = env[Config.googleRedirectUri],
    )
)

fun createService(
    env: Environment = Environment.ENV,
    internet: HttpHandler = JavaHttpClient(),
    clock: Clock = Clock.systemUTC(),
    random: Random = Random.Default,
) = TogglesApp(
    storage = Storage.dynamoDb(
        dynamoDb = DynamoDb.Http(
            region = RegionProvider.Environment(env)
                .orElse(RegionProvider.Profile(env))
                .orElseThrow(),
            credentialsProvider = CredentialsChain.Environment(env)
                .orElse(CredentialsChain.StsProfile(env))
                .provider(),
            http = internet,
            clock = clock
        ),
        tenantsTableName = env[Config.tenantsTableName],
        projectsTableName = env[Config.projectsTableName],
        togglesTableName = env[Config.togglesTableName],
        apiKeysTableName = env[Config.apiKeysTableName],
        usersTableName = env[Config.usersTableName],
        autoCreate = env[Config.dynamoDbStorageAutoCreate],
    ),
    pageSize = env[Config.pageSize],
    clock = clock,
    random = random,
    secretKey = env[Config.secretKeyBase64].use(String::base64DecodedArray)
)