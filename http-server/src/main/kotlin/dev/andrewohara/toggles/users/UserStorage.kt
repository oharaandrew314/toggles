package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UserId
import dev.andrewohara.toggles.UserNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface UserStorage {
    fun list(pageSize: Int): Paginator<User, String>
    fun list(tenantId: TenantId, pageSize: Int): Paginator<User, UserId>

    operator fun get(emailAddress: EmailAddress): User?
    operator fun get(tenantId: TenantId, userId: UserId): User?
    operator fun plusAssign(user: User)
    operator fun minusAssign(user: User)

    fun getOrFail(tenantId: TenantId, userId: UserId) =
        get(tenantId, userId).asResultOr { UserNotFound(tenantId, userId) }
}