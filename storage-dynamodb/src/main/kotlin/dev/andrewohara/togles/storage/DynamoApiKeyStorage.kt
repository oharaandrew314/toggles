package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.TokenMd5
import dev.andrewohara.toggles.storage.ApiKeyStorage
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
    table: DynamoDbTableMapper<DynamoApiKey, ProjectName, EnvironmentName>
) = object : ApiKeyStorage {
    override fun list(
        projectName: ProjectName,
        pageSize: Int,
    ) = Paginator<ApiKeyMeta, EnvironmentName> { cursor ->
        val page = table.primaryIndex().queryPage(
           HashKey = projectName,
           ExclusiveStartKey = cursor?.let {
               Key(ProjectName.attribute of projectName, EnvironmentName.attribute of cursor)
           },
           Limit = pageSize
        )

        Page(
           items = page.items.map { it.toModel() },
           next = page.lastEvaluatedKey?.let(EnvironmentName.attribute)
        )
    }

    override fun get(projectName: ProjectName, environment: EnvironmentName) =
        table[projectName, environment]?.toModel()

    override fun set(meta: ApiKeyMeta, tokenMd5: TokenMd5) = table.plusAssign(DynamoApiKey(
        projectName = meta.projectName,
        environment = meta.environment,
        createdOn = meta.createdOn,
        hash = tokenMd5.toString()
    ))

    override fun minusAssign(meta: ApiKeyMeta) =
        table.delete(meta.projectName, meta.environment)

    override fun get(tokenMd5: TokenMd5) = table
        .index(DynamoApiKey.lookupIndex)
        .query(tokenMd5.toString())
        .firstOrNull()
        ?.toModel()
}


@JsonSerializable
data class DynamoApiKey(
    val projectName: ProjectName,
    val environment: EnvironmentName,
    val createdOn: Instant,
    val hash: String
) {
    companion object {
        val lookupIndex = DynamoDbTableMapperSchema.GlobalSecondary<DynamoApiKey, String, Unit>(
            indexName = IndexName.of("lookup"),
            hashKeyAttribute = Attribute.string().required("hash"),
            sortKeyAttribute = null,
            lens = togglesJson.autoDynamoLens()
        )
    }
}

private fun DynamoApiKey.toModel() = ApiKeyMeta(
    projectName = projectName,
    environment = environment,
    createdOn = createdOn
)