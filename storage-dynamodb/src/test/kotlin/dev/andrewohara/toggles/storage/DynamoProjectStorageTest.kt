package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ProjectStorageContract
import dev.andrewohara.togles.storage.DynamoProjectStorage
import dev.andrewohara.togles.storage.dynamoDb
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.TableName

class DynamoProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = ProjectStorage.dynamoDb(
        dynamoDb = FakeDynamoDb().client(),
        tableName = TableName.of("projects"),
        autoCreate = true
    )
}