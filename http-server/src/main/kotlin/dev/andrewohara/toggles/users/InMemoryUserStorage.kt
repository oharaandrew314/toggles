package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

fun inMemoryUserStorage() = object: UserStorage {

    private val comparator = Comparator<User> { o1, o2 -> o1.uniqueId.compareTo(o2.uniqueId) }

    private val users = ConcurrentSkipListSet(comparator)

    override fun list(
        tenantId: TenantId, pageSize: Int
    ) = Paginator<User, UniqueId> { cursor ->
        val page = users
            .filter { it.tenantId == tenantId }
            .sortedWith(comparator)
            .dropWhile { cursor != null && it.uniqueId < cursor }
            .take(pageSize + 1)

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.uniqueId
        )
    }

    override fun get(tenantId: TenantId, uniqueId: UniqueId) = users.find { it.tenantId == tenantId && it.uniqueId == uniqueId }

    override fun get(emailAddress: EmailAddress) = users.find { it.emailAddress == emailAddress }

    override fun plusAssign(user: User) {
        users -= user
        users += user
    }

    override fun minusAssign(user: User) = users.minusAssign(user)
}