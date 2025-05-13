package dev.andrewohara.toggles.users

import dev.andrewohara.toggles.ServerContractBase
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.TogglesErrorDto
import dev.andrewohara.toggles.idp1Email4
import dev.andrewohara.toggles.idp2Email1
import dev.andrewohara.toggles.tenants.TenantCreateData
import dev.andrewohara.toggles.tenants.createTenant
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

abstract class UsersHttpContract: ServerContractBase(pageSize = 2) {
    
    @Test
    fun `list users - success`() {
        val page1 = httpClient(adminToken).listUsers(null).shouldBeSuccess()
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()
        
        val page2 = httpClient(adminToken).listUsers(page1.next).shouldBeSuccess()
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()
        
        page1.items.plus(page2.items)
            .shouldContainExactlyInAnyOrder(admin.toDto(), developer.toDto(), tester.toDto())
    }
    
    @Test
    fun `get user - success`() {
        httpClient(adminToken).getUser(developer.uniqueId) shouldBeSuccess developer.toDto()
    }
    
    @Test
    fun `get user - user not found`() {
        val userId = UniqueId.of("ABCDEFGH")
        httpClient(adminToken).getUser(userId) shouldBeFailure TogglesErrorDto(
            message = "User not found: $userId"
        )
    }

    @Test
    fun `invite user - success as admin`() {
        val user = httpClient(adminToken).inviteUser(
            UserInviteDataDto(idp1Email4, UserPermissionsDataDto(UserRoleDto.Tester))
        ).shouldBeSuccess()

        storage.users.list(tenant1.tenantId, 100).toList()
            .map { it.toDto() }
            .shouldContainExactlyInAnyOrder(admin.toDto(), developer.toDto(), tester.toDto(), user)
    }

    @Test
    fun `invite user - forbidden as developer`() {
        httpClient(developerToken)
            .inviteUser(UserInviteDataDto(idp1Email4, UserPermissionsDataDto(UserRoleDto.Tester)))
            .shouldBeFailure(TogglesErrorDto("You must be an admin to perform this action"))
    }

    @Test
    fun `invite user - forbidden as tester`() {
        httpClient(testerToken)
            .inviteUser(UserInviteDataDto(idp1Email4, UserPermissionsDataDto(UserRoleDto.Tester)))
            .shouldBeFailure(TogglesErrorDto("You must be an admin to perform this action"))
    }

    @Test
    fun `invite user - already exists in same tenant`() {
        httpClient(adminToken)
            .inviteUser(UserInviteDataDto(developer.emailAddress, UserPermissionsDataDto(UserRoleDto.Tester)))
            .shouldBeFailure(TogglesErrorDto("User already exists: ${developer.emailAddress}"))
    }

    @Test
    fun `invite user - already exists in another tenant`() {
        toggles.createTenant(TenantCreateData(idp2Email1)).shouldBeSuccess()

        httpClient(adminToken)
            .inviteUser(UserInviteDataDto(idp2Email1, UserPermissionsDataDto(UserRoleDto.Tester)))
            .shouldBeFailure(TogglesErrorDto("User already exists: $idp2Email1"))
    }

    @Test
    fun `delete user - wrong tenant`() {
        val otherTenant = toggles.createTenant(TenantCreateData(idp2Email1)).shouldBeSuccess()
        val otherTenantUser = storage.users.list(otherTenant.tenantId,100).first()

        httpClient(adminToken)
            .deleteUser(otherTenantUser.uniqueId)
            .shouldBeFailure(TogglesErrorDto("User not found: ${otherTenantUser.uniqueId}"))
    }

    @Test
    fun `delete user - not found`() {
        val uniqueId = UniqueId.of("ABCDEFGH")
        httpClient(adminToken)
            .deleteUser(uniqueId)
            .shouldBeFailure(TogglesErrorDto("User not found: $uniqueId"))
    }

    @Test
    fun `delete user - success as admin`() {
        httpClient(adminToken)
            .deleteUser(developer.uniqueId)
            .shouldBeSuccess(developer.toDto())

        storage.users.list(tenant1.tenantId, 100).toList()
            .shouldContainExactlyInAnyOrder(admin, tester)
    }

    @Test
    fun `delete user - cannot delete self`() {
        httpClient(adminToken)
            .deleteUser(admin.uniqueId)
            .shouldBeFailure(TogglesErrorDto("You cannot perform this action on yourself"))
    }

    @Test
    fun `delete user - forbidden as non-admin`() {
        httpClient(developerToken)
            .deleteUser(tester.uniqueId)
            .shouldBeFailure(TogglesErrorDto("You must be an admin to perform this action"))
    }

    @Test
    fun `delete user - forbidden cross-tenant`() {
        httpClient(testerToken)
            .deleteUser(developer.uniqueId)
            .shouldBeFailure(TogglesErrorDto("You must be an admin to perform this action"))
    }

    @Test
    fun `update permissions - cannot change own`() {
        httpClient(adminToken)
            .updatePermissions(admin.uniqueId,UserPermissionsDataDto(UserRoleDto.Admin))
            .shouldBeFailure(TogglesErrorDto("You cannot perform this action on yourself"))
    }

    @Test
    fun `update permissions - wrong tenant`() {
        val otherTenant = toggles.createTenant(TenantCreateData(idp2Email1)).shouldBeSuccess()
        val otherTenantUser = storage.users.list(otherTenant.tenantId,100).first()

        httpClient(adminToken)
            .updatePermissions(otherTenantUser.uniqueId, UserPermissionsDataDto(UserRoleDto.Admin))
            .shouldBeFailure(TogglesErrorDto("User not found: ${otherTenantUser.uniqueId}"))
    }

    @Test
    fun `update permissions - user not found`() {
        val uniqueId = UniqueId.of("ABCDEFGH")

        httpClient(adminToken)
            .updatePermissions(uniqueId, UserPermissionsDataDto(UserRoleDto.Admin))
            .shouldBeFailure(TogglesErrorDto("User not found: $uniqueId"))
    }

    @Test
    fun `update permissions - success as admin`() {
        val expected = developer.copy(role = UserRole.Admin)

        httpClient(adminToken)
            .updatePermissions(developer.uniqueId, UserPermissionsDataDto(UserRoleDto.Admin))
            .shouldBeSuccess(expected.toDto())

        storage.users[tenant1.tenantId, developer.uniqueId] shouldBe expected
    }

    @Test
    fun `update permissions - forbidden as developer`() {
        httpClient(developerToken)
            .updatePermissions(tester.uniqueId, UserPermissionsDataDto(UserRoleDto.Developer))
            .shouldBeFailure(TogglesErrorDto("You must be an admin to perform this action"))
    }

    @Test
    fun `update permissions - forbidden as tester`() {
        httpClient(testerToken)
            .updatePermissions(developer.uniqueId, UserPermissionsDataDto(UserRoleDto.Tester))
            .shouldBeFailure(TogglesErrorDto("You must be an admin to perform this action"))
    }
}