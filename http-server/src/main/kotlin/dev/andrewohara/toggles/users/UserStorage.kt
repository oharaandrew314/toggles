package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.UserNotFound
import dev.andrewohara.toggles.UserNotFoundByEmail
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface UserStorage {
    fun list(tenantId: TenantId, pageSize: Int): Paginator<User, UniqueId>

    operator fun get(emailAddress: EmailAddress): User?
    operator fun get(tenantId: TenantId, uniqueId: UniqueId): User?
    operator fun plusAssign(user: User)
    operator fun minusAssign(user: User)

    fun getOrFail(tenantId: TenantId, uniqueId: UniqueId) =
        get(tenantId, uniqueId).asResultOr { UserNotFound(tenantId,uniqueId) }

    fun getOrFail(emailAddress: EmailAddress) =
        get(emailAddress).asResultOr { UserNotFoundByEmail(emailAddress) }
}