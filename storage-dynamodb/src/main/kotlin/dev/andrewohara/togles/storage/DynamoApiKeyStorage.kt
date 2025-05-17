package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.ApiKeyHash
import dev.andrewohara.toggles.apikeys.ApiKeyStorage
import dev.andrewohara.utils.pagination.Page
import dev.andrewohara.utils.pagination.Paginator
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapper
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.mapper.plusAssign
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.format.autoDynamoLens
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

fun dynamoApiKeyStorage(
    table: DynamoDbTableMapper<DynamoApiKey, ProjectRef, EnvironmentName>
) = object : ApiKeyStorage {
    override fun list(
        tenantId: TenantId,
        projectName: ProjectName,
        pageSize: Int,
    ) = Paginator<ApiKeyMeta, EnvironmentName> { cursor ->
        val page = table.primaryIndex().queryPage(
           HashKey = ProjectRef(tenantId, projectName),
           ExclusiveStartKey = cursor?.let {
               Key(
                   projectRefAttr of ProjectRef(tenantId, projectName),
                   EnvironmentName.attribute of cursor
               )
           },
           Limit = pageSize
        )

        Page(
           items = page.items.map { it.toModel() },
           next = page.lastEvaluatedKey?.let(EnvironmentName.attribute)
        )
    }

    override fun get(tenantId: TenantId, projectName: ProjectName, environment: EnvironmentName) =
        table[ProjectRef(tenantId, projectName), environment]?.toModel()

    override fun set(meta: ApiKeyMeta, apiKeyHash: ApiKeyHash) = table.plusAssign(DynamoApiKey(
        projectRef = projectRefMapping(ProjectRef(meta.tenantId, meta.projectName)),
        environmentName = meta.environment,
        createdOn = meta.createdOn,
        hashBase64 = apiKeyHash.base64
    ))

    override fun minusAssign(meta: ApiKeyMeta) =
        table.delete(ProjectRef(meta.tenantId, meta.projectName), meta.environment)

    override fun get(apiKeyHash: ApiKeyHash) = table
        .index(DynamoApiKey.lookupIndex)
        .query(apiKeyHash.toString())
        .firstOrNull()
        ?.toModel()
}


@JsonSerializable
data class DynamoApiKey(
    val projectRef: String,
    val environmentName: EnvironmentName,
    val createdOn: Instant,
    val hashBase64: String
) {
    companion object {
        val lookupIndex = DynamoDbTableMapperSchema.GlobalSecondary<DynamoApiKey, String, Unit>(
            indexName = IndexName.of("lookup"),
            hashKeyAttribute = Attribute.string().required("hashBase64"),
            sortKeyAttribute = null,
            lens = dynamoJson.autoDynamoLens()
        )
    }
}

private fun DynamoApiKey.toModel() = ApiKeyMeta(
    tenantId = projectRefMapping(projectRef).tenantId,
    projectName = projectRefMapping(projectRef).projectName,
    environment = environmentName,
    createdOn = createdOn
)