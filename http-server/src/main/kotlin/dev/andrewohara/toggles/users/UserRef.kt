package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UserId
import org.http4k.lens.BiDiMapping

typealias UserRef = Pair<TenantId, UserId>

val userRefMapping = BiDiMapping(UserRef::class.java,
    asOut = { text: String ->
        val (tenantId, userId) = text.split("/")
        TenantId.parse(tenantId) to UserId.parse(userId)
    },
    asIn = { (tenantId, userId) -> "${tenantId.value}/${userId.value}" }
)