package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ToggleStorageContract
import dev.andrewohara.togles.storage.dynamoDb
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName

class DynamoToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = ToggleStorage.dynamoDb(
        dynamoDb = FakeDynamoDb().client(),
        tableName = TableName.of("toggles"),
        autoCreate = true
    )
}