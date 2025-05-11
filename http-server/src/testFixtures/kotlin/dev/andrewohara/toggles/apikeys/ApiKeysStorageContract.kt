package dev.andrewohara.toggles.apikeys

import dev.andrewohara.toggles.StorageContractBase
import dev.andrewohara.toggles.TenantId
import dev.andrewohara.toggles.dev
import dev.andrewohara.toggles.devAndProd
import dev.andrewohara.toggles.prod
import dev.andrewohara.toggles.projectName1
import dev.andrewohara.toggles.projectName2
import dev.andrewohara.toggles.projects.Project
import dev.andrewohara.toggles.staging
import dev.andrewohara.toggles.tenants.Tenant
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

private const val SHA_256_SIZE = 32

@OptIn(ExperimentalStdlibApi::class)
private fun nextApiKeyHash(random: Random) = ApiKeyHash.parse(random.nextBytes(SHA_256_SIZE).toHexString())

abstract class ApiKeysStorageContract: StorageContractBase() {

    private lateinit var tenant1: Tenant

    private lateinit var project1: Project
    private lateinit var project2: Project

    private val project1DevKey = nextApiKeyHash(random)
    private lateinit var project1Dev: ApiKeyMeta

    private val project2DevKey =nextApiKeyHash(random)
    private lateinit var project2Dev: ApiKeyMeta

    private val project2StagingKey = nextApiKeyHash(random)
    private lateinit var project2Staging: ApiKeyMeta

    private val project2ProdKey = nextApiKeyHash(random)
    private lateinit var project2Prod: ApiKeyMeta

    @BeforeEach
    override fun setup() {
        super.setup()

        tenant1 = Tenant(
            tenantId = TenantId.Companion.random(random),
            createdOn = time
        ).also(storage.tenants::plusAssign)

        project1 = Project(
            tenantId = tenant1.tenantId,
            projectName = projectName1,
            createdOn = time,
            updatedOn = time,
            environments = devAndProd
        ).also(storage.projects::plusAssign)

        project2 = Project(
            tenantId = tenant1.tenantId,
            projectName = projectName2,
            createdOn = time + Duration.ofMinutes(1),
            updatedOn = time + Duration.ofMinutes(1),
            environments = listOf(dev, staging, prod)
        ).also(storage.projects::plusAssign)

        project1Dev = ApiKeyMeta(
            tenantId = tenant1.tenantId,
            projectName = projectName1,
            environment = dev,
            createdOn = project1.createdOn.plusSeconds(1)
        ).also { storage.apiKeys[it] = project1DevKey }

        project2Dev = ApiKeyMeta(
            tenantId = tenant1.tenantId,
            projectName = projectName2,
            environment = dev,
            createdOn = project2.createdOn.plusSeconds(1)
        ).also { storage.apiKeys[it] = project2DevKey }

        project2Staging = ApiKeyMeta(
            tenantId = tenant1.tenantId,
            projectName = projectName2,
            environment = staging,
            createdOn = project2.createdOn.plusSeconds(2)
        ).also { storage.apiKeys[it] = project2StagingKey }

        project2Prod = ApiKeyMeta(
            tenantId = tenant1.tenantId,
            projectName = projectName2,
            environment = prod,
            createdOn = project2.createdOn.plusSeconds(3)
        ).also { storage.apiKeys[it] = project2ProdKey }
    }

    @Test
    fun `list api keys - all`() {
        storage.apiKeys.list(tenant1.tenantId, projectName2, 2).toList()
            .shouldContainExactlyInAnyOrder(project2Dev, project2Staging, project2Prod)
    }

    @Test
    fun `list api keys - paged`() {
        val page1 = storage.apiKeys.list(tenant1.tenantId, projectName2, pageSize = 2)[null]
        page1.items.shouldHaveSize(2)
        page1.next.shouldNotBeNull()

        val page2 = storage.apiKeys.list(tenant1.tenantId, projectName2, pageSize = 2)[page1.next]
        page2.items.shouldHaveSize(1)
        page2.next.shouldBeNull()
    }

    @Test
    fun `get api key - found`() {
        storage.apiKeys[tenant1.tenantId, projectName1, dev] shouldBe project1Dev
    }

    @Test
    fun `get api key - not found`() {
        storage.apiKeys[tenant1.tenantId, projectName1, prod].shouldBeNull()
    }

    @Test
    fun `delete api key - found`() {
        storage.apiKeys -= project1Dev
        storage.apiKeys.list(tenant1.tenantId, projectName1, 100).shouldBeEmpty()
    }

    @Test
    fun `delete api key - not found`() {
        storage.apiKeys -= project1Dev.copy(environment = staging)
        storage.apiKeys.list(tenant1.tenantId, projectName1, 100).shouldContainExactly(project1Dev)
    }

    @Test
    fun `exchange - not found`() {
        storage.apiKeys[nextApiKeyHash(random)].shouldBeNull()
    }

    @Test
    fun `exchange - found`() {
        storage.apiKeys[project1DevKey] shouldBe project1Dev
    }

    @Test
    fun `create new`() {
        val newKey = nextApiKeyHash(random)
        val project1Prod = ApiKeyMeta(
            tenantId = tenant1.tenantId,
            projectName = projectName1,
            environment = prod,
            createdOn = project1.createdOn.plusSeconds(4)
        )

        storage.apiKeys[project1Prod] = newKey

        storage.apiKeys[tenant1.tenantId, projectName1, prod] shouldBe project1Prod
        storage.apiKeys[newKey] shouldBe project1Prod
    }

    @Test
    fun `update existing`() {
        val newKey = nextApiKeyHash(random)

        storage.apiKeys[project1Dev] = newKey

        storage.apiKeys[project1DevKey].shouldBeNull()
        storage.apiKeys[newKey] shouldBe project1Dev
    }
}