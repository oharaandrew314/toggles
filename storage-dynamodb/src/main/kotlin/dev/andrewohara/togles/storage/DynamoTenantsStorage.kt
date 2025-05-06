package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.tenants.Tenant
import dev.andrewohara.toggles.tenants.TenantStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.model.Key
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

internal fun dynamoTenantsStorage(
    table: DynamoDbTableMapper<DynamoTenant, TenantId, Unit>
) = object : TenantStorage {

    override fun list(pageSize: Int) = Paginator<Tenant, TenantId> { cursor ->
        val page = table.primaryIndex().scanPage(
            ExclusiveStartKey = cursor?.let { Key(TenantId.attribute of it) },
            Limit = pageSize
        )

        Page(
            items = page.items.map { it.toModel() },
            next = page.lastEvaluatedKey?.let(TenantId.attribute)
        )
    }

    override fun get(tenantId: TenantId) = table[tenantId]?.toModel()

    override fun plusAssign(tenant: Tenant) {
        table += DynamoTenant(
            tenantId = tenant.tenantId,
            createdOn = tenant.createdOn
        )
    }

    override fun minusAssign(tenant: Tenant) = table.delete(tenant.tenantId)
}

@JsonSerializable
data class DynamoTenant(
    val tenantId: TenantId,
    val createdOn: Instant
)

private fun DynamoTenant.toModel() = Tenant(
    tenantId = tenantId,
    createdOn = createdOn
)