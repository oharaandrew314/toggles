package dev.andrewohara.toggles

import org.http4k.config.EnvironmentKey
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.lens.boolean
import org.http4k.lens.int
import org.http4k.lens.port
import org.http4k.lens.secret
import org.http4k.lens.string
import org.http4k.lens.uri
import org.http4k.lens.value

object Config {
    // general
    val secretKeyBase64 = EnvironmentKey.secret().required("SECRET_KEY_BASE64")
    val pageSize = EnvironmentKey.int().defaulted("PAGE_SIZE", 100)
    val debugPort = EnvironmentKey.port().optional("DEBUG_PORT")

    // google sign in
    val googleClientId = EnvironmentKey.string().required("GOOGLE_CLIENT_ID")
    val googleRedirectUri = EnvironmentKey.uri().required("GOOGLE_REDIRECT_URI")

    // Dynamo DB Storage
    val dynamoDbStorageAutoCreate = EnvironmentKey.boolean().defaulted("DYNAMODB_STORAGE_AUTO_CREATE", false)
    val projectsTableName = EnvironmentKey.value(TableName).required("PROJECTS_TABLE_NAME")
    val togglesTableName = EnvironmentKey.value(TableName).required("TOGGLES_TABLE_NAME")
    val tenantsTableName = EnvironmentKey.value(TableName).required("TENANTS_TABLE_NAME")
    val usersTableName = EnvironmentKey.value(TableName).required("USERS_TABLE_NAME")
    val apiKeysTableName = EnvironmentKey.value(TableName).required("API_KEYS_TABLE_NAME")
}