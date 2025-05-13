package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.StorageContractBase
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.idp1Email1
import dev.andrewohara.toggles.idp1Email2
import dev.andrewohara.toggles.idp1Email3
import dev.andrewohara.toggles.idp2Email1
import dev.andrewohara.toggles.tenants.Tenant
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class UserStorageContract:  StorageContractBase() {

    private lateinit var tenant1: Tenant
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    private lateinit var tenant2: Tenant
    private lateinit var user4: User

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = Tenant(TenantId.Companion.random(random), time)
            .also(storage.tenants::plusAssign)
        user1 = User(tenant1.tenantId, nextUniqueId(), idp1Email1, time, UserRole.Admin)
            .also(storage.users::plusAssign)
        user2 = User(tenant1.tenantId, nextUniqueId(), idp1Email2, time, UserRole.Developer)
            .also(storage.users::plusAssign)
        user3 = User(tenant1.tenantId, nextUniqueId(), idp1Email3, time, UserRole.Tester)
            .also(storage.users::plusAssign)

        tenant2 =  Tenant(TenantId.Companion.random(random), time)
            .also(storage.tenants::plusAssign)
        user4 = User(tenant2.tenantId, nextUniqueId(), idp2Email1, time, UserRole.Admin)
            .also(storage.users::plusAssign)
    }

    @Test
    fun `list users - all`() {
        storage.users.list(tenant1.tenantId, 2).toList()
            .shouldContainExactlyInAnyOrder(user1, user2, user3)
    }

    @Test
    fun `list users - paged`() {
        val page1 = storage.users.list(tenant1.tenantId, 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.users.list(tenant1.tenantId, 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()
    }

    @Test
    fun `get user - found`() {
        storage.users[user1.tenantId, user1.uniqueId] shouldBe user1
    }

    @Test
    fun `get user - not found`() {
        storage.users[tenant2.tenantId, user1.uniqueId].shouldBeNull()
    }

    @Test
    fun `get user by email - found`() {
        storage.users[user4.emailAddress] shouldBe user4
    }

    @Test
    fun `get user by email - not found`() {
        storage.users[EmailAddress.Companion.of("not@found.com")].shouldBeNull()
    }

    @Test
    fun `delete user - found`() {
        storage.users -= user1

        storage.users[user1.tenantId, user1.uniqueId].shouldBeNull()
        storage.users.list(tenant1.tenantId, 100).toList()
            .shouldContainExactlyInAnyOrder(user2, user3)
    }

    @Test
    fun `delete user - not found`() {
        storage.users -= user1
        storage.users -= user1
    }

    @Test
    fun `update user`() {
        storage.users += user2.copy(role = UserRole.Admin)
        storage.users[user2.tenantId, user2.uniqueId] shouldBe user2.copy(role = UserRole.Admin)
    }
}