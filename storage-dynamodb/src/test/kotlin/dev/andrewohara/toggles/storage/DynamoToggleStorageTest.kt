package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ToggleStorageContract
import dev.andrewohara.togles.storage.DynamoToggleStorage
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName

class DynamoToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = DynamoToggleStorage(
        dynamoDb = FakeDynamoDb().client(),
        tableName = TableName.of("toggles"),
        autoCreate = true
    )
}