package org.master.cache

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ClientPngTest {

    @Test
    fun notNullRequest() {
        val client = ClientPng()
        var bytes: ByteArray? = null
        val job = GlobalScope.launch {
            bytes = client.get("https://www.openstreetmap.org/")

        }
        job.invokeOnCompletion {
            assertNotNull(bytes)
        }
    }
}