package dev.andrewohara.toggles.storage

import dev.andrewohara.toggles.projects.ProjectStorageContract
import dev.andrewohara.toggles.Storage
import dev.andrewohara.toggles.apikeys.ApiKeysStorageContract
import dev.andrewohara.toggles.toggles.ToggleStorageContract
import dev.andrewohara.togles.storage.dynamoDb
import org.http4k.aws.AwsCredentials
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.HttpHandler
import org.http4k.filter.debug

fun testDynamoStorage(internet: HttpHandler = FakeDynamoDb()) = Storage.dynamoDb(
    dynamoDb = DynamoDb.Http(Region.CA_CENTRAL_1, { AwsCredentials("accessKey", "secret") }, internet),
    projectsTableName = TableName.of("projects"),
    togglesTableName = TableName.of("toggles"),
    apiKeysTableName = TableName.of("api-keys"),
    tenantsTableName = TableName.of("tenants"),
    usersTableName = TableName.of("users"),
    autoCreate = true
)

class DynamoToggleStorageTest: ToggleStorageContract() {
    private val dynamo = FakeDynamoDb()

    override fun createStorage() = testDynamoStorage(dynamo.debug())
}

class DynamoProjectStorageTest: ProjectStorageContract() {
    override fun createStorage() = testDynamoStorage()
}

class DynamoApiKeyStorageTest: ApiKeysStorageContract() {
    private val dynamo = FakeDynamoDb()

    override fun createStorage() = testDynamoStorage(dynamo.debug())
}

//class DynamoUserStorageTest: ProjectStorageContract() {
//    override fun createStorage() = testDynamoStorage()
//}

//class DynamoTenantStorageTest: ProjectStorageContract() {
//    override fun createStorage() = testDynamoStorage()
//}