package dev.andrewohara.toggles

import dev.andrewohara.toggles.apikeys.ApiKeyMeta
import dev.andrewohara.toggles.apikeys.TokenMd5
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.random.Random

private const val MD5_SIZE = 16

abstract class ApiKeysStorageContract: StorageContractBase() {

    private val random = Random(1337)

    private lateinit var project1: Project
    private lateinit var project2: Project

    private val project1DevKey = TokenMd5.of(random.nextBytes(MD5_SIZE))
    private lateinit var project1Dev: ApiKeyMeta

    private val project2DevKey = TokenMd5.of(random.nextBytes(MD5_SIZE))
    private lateinit var project2Dev: ApiKeyMeta

    private val project2StagingKey = TokenMd5.of(random.nextBytes(MD5_SIZE))
    private lateinit var project2Staging: ApiKeyMeta

    private val project2ProdKey = TokenMd5.of(random.nextBytes(MD5_SIZE))
    private lateinit var project2Prod: ApiKeyMeta

    @BeforeEach
    override fun setup() {
        super.setup()

        project1 = Project(
            projectName = projectName1,
            createdOn = t0,
            updatedOn = t0,
            environments = devAndProd
        ).also(storage.projects::plusAssign)

        project2 = Project(
            projectName = projectName2,
            createdOn = t0 + Duration.ofMinutes(1),
            updatedOn = t0 + Duration.ofMinutes(1),
            environments = listOf(dev, staging, prod)
        ).also(storage.projects::plusAssign)

        project1Dev = ApiKeyMeta(
            projectName = projectName1,
            environment = dev,
            createdOn = project1.createdOn.plusSeconds(1)
        ).also { storage.apiKeys[it] = project1DevKey }

        project2Dev = ApiKeyMeta(
            projectName = projectName2,
            environment = dev,
            createdOn = project2.createdOn.plusSeconds(1)
        ).also { storage.apiKeys[it] = project2DevKey }

        project2Staging = ApiKeyMeta(
            projectName = projectName2,
            environment = staging,
            createdOn = project2.createdOn.plusSeconds(2)
        ).also { storage.apiKeys[it] = project2StagingKey }

        project2Prod = ApiKeyMeta(
            projectName = projectName2,
            environment = prod,
            createdOn = project2.createdOn.plusSeconds(3)
        ).also { storage.apiKeys[it] = project2ProdKey }
    }

    @Test
    fun `list api keys - all`() {
        storage.apiKeys.list(projectName2, 2).toList()
            .shouldContainExactlyInAnyOrder(project2Dev, project2Staging, project2Prod)
    }

    @Test
    fun `list api keys - paged`() {
        val page1 = storage.apiKeys.list(projectName2, pageSize = 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.apiKeys.list(projectName2, pageSize = 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()
    }

    @Test
    fun `get api key - found`() {
        storage.apiKeys[projectName1, dev] shouldBe project1Dev
    }

    @Test
    fun `get api key - not found`() {
        storage.apiKeys[projectName1, prod].shouldBeNull()
    }

    @Test
    fun `delete api key - found`() {
        storage.apiKeys -= project1Dev
        storage.apiKeys.list(projectName1, 100).shouldBeEmpty()
    }

    @Test
    fun `delete api key - not found`() {
        storage.apiKeys -= project1Dev.copy(environment = staging)
        storage.apiKeys.list(projectName1, 100).shouldContainExactly(project1Dev)
    }

    @Test
    fun `exchange - not found`() {
        storage.apiKeys[TokenMd5.of(random.nextBytes(MD5_SIZE))].shouldBeNull()
    }

    @Test
    fun `exchange - found`() {
        storage.apiKeys[project1DevKey] shouldBe project1Dev
    }

    @Test
    fun `create new`() {
        val newKey = TokenMd5.of(random.nextBytes(MD5_SIZE))
        val project1Prod = ApiKeyMeta(
            projectName = projectName1,
            environment = prod,
            createdOn = project1.createdOn.plusSeconds(4)
        )

        storage.apiKeys[project1Prod] = newKey

        storage.apiKeys[projectName1, prod] shouldBe project1Prod
        storage.apiKeys[newKey] shouldBe project1Prod
    }

    @Test
    fun `update existing`() {
        val newKey = TokenMd5.of(random.nextBytes(MD5_SIZE))

        storage.apiKeys[project1Dev] = newKey

        storage.apiKeys[project1DevKey].shouldBeNull()
        storage.apiKeys[newKey] shouldBe project1Dev
    }
}