package dev.andrewohara.toggles.tenants

import dev.andrewohara.toggles.ServerContractBase
import dev.andrewohara.toggles.idp2Email1
import org.junit.jupiter.api.Test

abstract class TenantsHttpContract: ServerContractBase() {
    @Test
    fun `create tenant - success`() {
        val token = createToken(idp2Email1)
    }

    @Test
    fun `create tenant - already has tenant`() {

    }
}