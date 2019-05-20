package org.master.cache.cache

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class DiskApiImplTest {


    @Test
    fun testWrite() {
        val diskApi = DiskApiImpl()
        val list = diskApi.writeFile("/text", "kukuk".toByteArray()).flatMapMany {
            diskApi.readFile("/text.png")
        }.collectList()

        StepVerifier.create(list).consumeNextWith {
            assertNotNull(it)
        }.verifyComplete()
    }

    @Test
    fun testRead() {
        val diskApi = DiskApiImpl()
        val list = diskApi.readFile("/test.txt").collectList()
        StepVerifier.create(list).consumeNextWith {
            assertNotNull(it)
        }.verifyComplete()
    }

    @Test
    fun getFiles() {
        val diskApi = DiskApiImpl()
        val list = diskApi.getFiles()
        StepVerifier.create(list).consumeNextWith {
            assertNotNull(it)
        }.verifyComplete()
    }
}