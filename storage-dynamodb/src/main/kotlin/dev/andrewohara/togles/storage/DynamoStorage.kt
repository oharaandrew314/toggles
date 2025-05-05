package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.EnvironmentName
import dev.andrewohara.toggles.ProjectName
import dev.andrewohara.toggles.ToggleName
import dev.andrewohara.toggles.Storage
import dev.andrewohara.toggles.TenantId
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.flatMapFailure
import dev.forkhandles.result4k.onFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.describeTable
import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema.Primary
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.format.autoDynamoLens

fun Storage.Companion.dynamoDb(
    dynamoDb: DynamoDb,
    projectsTableName: TableName,
    togglesTableName: TableName,
    apiKeysTableName: TableName,
    autoCreate: Boolean = false
): Storage {
    val projects = dynamoDb.tableMapper(
        projectsTableName, Primary<DynamoProject, TenantId, ProjectName>(
            hashKeyAttribute = TenantId.attribute,
            sortKeyAttribute = ProjectName.attribute,
            lens = togglesJson.autoDynamoLens()
        )
    )

    val toggles = dynamoDb.tableMapper(
        togglesTableName, Primary<DynamoToggle, ProjectRef, ToggleName>(
            hashKeyAttribute = projectRefAttr,
            sortKeyAttribute = ToggleName.attribute,
            lens = togglesJson.autoDynamoLens()
        )
    )

    val apiKeys = dynamoDb.tableMapper(
        apiKeysTableName, Primary<DynamoApiKey, ProjectRef, EnvironmentName>(
            hashKeyAttribute = projectRefAttr,
            sortKeyAttribute = EnvironmentName.attribute,
            lens = togglesJson.autoDynamoLens()
        )
    )

    dynamoDb.describeTable(projectsTableName)
        .flatMapFailure { if (autoCreate) projects.createTable() else it.asFailure() }
        .onFailure { it.reason.throwIt() }
    dynamoDb.describeTable(togglesTableName)
        .flatMapFailure { if (autoCreate) toggles.createTable() else it.asFailure() }
        .onFailure { it.reason.throwIt() }
    dynamoDb.describeTable(apiKeysTableName)
        .flatMapFailure { if (autoCreate) apiKeys.createTable(DynamoApiKey.lookupIndex) else it.asFailure() }
        .onFailure { it.reason.throwIt() }

    return Storage(
        projects = dynamoProjectStorage(projects),
        toggles = dynamoToggleStorage(toggles),
        apiKeys = dynamoApiKeyStorage(apiKeys),
        users = TODO(),
        tenants = TODO()
    )
}