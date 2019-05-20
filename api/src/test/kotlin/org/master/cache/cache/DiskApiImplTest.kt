package org.master.cache.cache

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class DiskApiImplTest {


    @Test
    fun testRead() {
        val diskApi = DiskApiImpl()
        val list = diskApi.testRead("/test.txt").collectList()
        StepVerifier.create(list).consumeNextWith {
            assertNotNull(it)
        }.verifyComplete()
    }


}