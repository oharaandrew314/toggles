package dev.andrewohara.toggles

import dev.andrewohara.toggles.storage.testDynamoStorage

class DynamoTogglesHttpTest: TogglesHttpContract() {
    override fun createStorage() = testDynamoStorage()
}