package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.toggles.ToggleStorageContract
import dev.andrewohara.togles.storage.dynamoDb
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName

private fun create() = Storage.dynamoDb(
    dynamoDb = FakeDynamoDb().client(),
    projectsTableName = TableName.of("projects"),
    togglesTableName = TableName.of("toggles"),
    apiKeysTableName = TableName.of("api-keys"),
    autoCreate = true
)

class DynamoToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = create()
}

class DynamoProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = create()
}

class DynamoApiKeyStorageTest: ProjectStorageContract() {
    override fun createStorage() = create()
}