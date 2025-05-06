package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UserId
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import java.util.concurrent.ConcurrentSkipListSet

fun inMemoryUserStorage() = object: UserStorage {

    private val comparator = Comparator<User> { o1, o2 -> o1.emailAddress.compareTo(o2.emailAddress) }

    private val users = ConcurrentSkipListSet(comparator)

    override fun list(pageSize: Int) = Paginator<User, String> { cursor ->
        val page = users
            .sortedWith(comparator)
            .dropWhile { cursor != null && it.userId.toString() <= cursor }
            .take(pageSize + 1)

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.userId?.toString()
        )
    }

    override fun list(
        tenantId: TenantId, pageSize: Int
    ) = Paginator<User, UserId> { cursor ->
        val page = users
            .filter { it.tenantId == tenantId }
            .sortedWith(comparator)
            .dropWhile { cursor != null && it.userId <= cursor }
            .take(pageSize + 1)

        Page(
            items = page.take(pageSize),
            next = page.drop(pageSize).firstOrNull()?.userId
        )
    }

    override fun get(tenantId: TenantId, userId: UserId) = users.find { it.userId == userId }

    override fun get(emailAddress: EmailAddress) = users.find { it.emailAddress == emailAddress }

    override fun plusAssign(user: User) = users.plusAssign(user)

    override fun minusAssign(user: User) = users.minusAssign(user)
}