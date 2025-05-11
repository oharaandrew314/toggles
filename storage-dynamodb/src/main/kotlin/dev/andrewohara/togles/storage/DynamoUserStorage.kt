package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EmailAddress
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.UniqueId
import dev.andrewohara.toggles.users.User
import dev.andrewohara.toggles.users.UserRole
import dev.andrewohara.toggles.users.UserStorage
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
    table: DynamoDbTableMapper<DynamoUser, TenantId, UniqueId>
): UserStorage = object : UserStorage {

    override fun list(
        tenantId: TenantId,
        pageSize: Int,
    ) = Paginator<User, UniqueId> { cursor ->
        val page = table.primaryIndex().queryPage(
            HashKey = tenantId,
            ExclusiveStartKey = cursor?.let { Key(TenantId.attribute of tenantId,UniqueId.attribute of cursor) },
            Limit = pageSize
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(UniqueId.attribute)
        )
    }

    override fun get(tenantId: TenantId, uniqueId: UniqueId) = table[tenantId, uniqueId]?.toModel()

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
            uniqueId = user.uniqueId,
            emailAddress = user.emailAddress,
            createdOn = user.createdOn,
            role = user.role.toString()
        )
    }

    override fun minusAssign(user: User) = table.delete(user.tenantId, user.uniqueId)
}

@JsonSerializable
internal data class DynamoUser(
    val tenantId: TenantId,
    val uniqueId: UniqueId,
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
    uniqueId = uniqueId,
    emailAddress = emailAddress,
    createdOn = createdOn,
    role = UserRole.valueOf(role)
)