package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UserId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.UserRole
import dev.andrewohara.toggles.users.UserStorage
import dev.andrewohara.toggles.users.userRefMapping
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.format.autoDynamoLens
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

internal fun dynamoUserStorage(
    table: DynamoDbTableMapper<DynamoUser, TenantId, UserId>
): UserStorage = object : UserStorage {

    override fun list(pageSize: Int) = Paginator<User, String> { cursor ->
        val page = table.primaryIndex().scanPage(
            ExclusiveStartKey = cursor?.let {
                val (tenantId, userId) = userRefMapping(cursor)
                Key(TenantId.attribute of tenantId, UserId.attribute of userId)
            },
            Limit = pageSize
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let {
                userRefMapping(TenantId.attribute(it) to UserId.attribute(it))
            }
        )
    }

    override fun list(
        tenantId: TenantId,
        pageSize: Int,
    ) = Paginator<User, UserId> { cursor ->
        val page = table.primaryIndex().queryPage(
            HashKey = tenantId,
            ExclusiveStartKey = cursor?.let { Key(UserId.attribute of cursor) },
            Limit = pageSize
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(UserId.attribute)
        )
    }

    override fun get(tenantId: TenantId, userId: UserId) = table[tenantId, userId]?.toModel()

    override fun get(emailAddress: EmailAddress): User? {
        return table
            .index(DynamoUser.emailIndex)
            .query(hashKey = emailAddress)
            .firstOrNull()
            ?.toModel()
    }

    override fun plusAssign(user: User) {
        table += DynamoUser(
            tenantId = user.tenantId,
            userId = user.userId,
            emailAddress = user.emailAddress,
            createdOn = user.createdOn,
            role = user.role.toString()
        )
    }

    override fun minusAssign(user: User) = table.delete(user.tenantId, user.userId)
}

@JsonSerializable
internal data class DynamoUser(
    val tenantId: TenantId,
    val userId: UserId,
    val emailAddress: EmailAddress,
    val createdOn: Instant,
    val role: String
) {
    companion object {
        val emailIndex = DynamoDbTableMapperSchema.GlobalSecondary<DynamoUser, EmailAddress, Unit>(
            indexName = IndexName.of("email"),
            hashKeyAttribute = EmailAddress.attribute,
            sortKeyAttribute = null,
            lens = dynamoJson.autoDynamoLens()
        )
    }
}

private fun DynamoUser.toModel() = User(
    tenantId = tenantId,
    userId = userId,
    emailAddress = emailAddress,
    createdOn = createdOn,
    role = UserRole.valueOf(role)
)