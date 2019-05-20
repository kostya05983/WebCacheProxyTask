package org.master.cache

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ClientPngTest {

    @Test
    fun notNullRequest() {
        val client = ClientPng()

        runBlocking {
            val bytes = client.get("/4/6/6.png")
            assertNotNull(bytes)
        }
    }
}