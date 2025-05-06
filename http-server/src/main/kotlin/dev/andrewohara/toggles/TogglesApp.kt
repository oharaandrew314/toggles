package dev.andrewohara.toggles

import dev.andrewohara.toggles.users.UserAuthorizer
import java.time.Clock
import kotlin.random.Random

class TogglesApp(
    val storage: Storage,
    val pageSize: Int = 100,
    val clock: Clock = Clock.systemUTC(),
    val random: Random = Random.Default,
    val secretKey: ByteArray,
    val userAuthorizer: UserAuthorizer
)