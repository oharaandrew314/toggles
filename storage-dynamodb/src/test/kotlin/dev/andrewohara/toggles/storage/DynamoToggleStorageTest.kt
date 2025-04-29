package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.ToggleStorageContract
import dev.andrewohara.togles.storage.DynamoToggleStorage
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.TableName

class DynamoToggleStorageTest: ToggleStorageContract() {
    override fun createStorage() = FakeDynamoDb()
        .client()
        .tableMapper(TableName.of("toggles"), DynamoToggleStorage.primaryIndex)
        .also { it.createTable().shouldBeSuccess() }
        .let { DynamoToggleStorage(it) }
}