package dev.andrewohara.togles.storage

import dev.andrewohara.toggles.storage.Storage
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName

fun Storage.Companion.dynamoDb(
    dynamoDb: DynamoDb,
    projectsTableName: TableName,
    togglesTableName: TableName,
    autoCreate: Boolean = false
) = Storage(
    projects = dynamoProjectStorage(dynamoDb, projectsTableName, autoCreate),
    toggles = dynamoToggleStorage(dynamoDb, togglesTableName, autoCreate)
)