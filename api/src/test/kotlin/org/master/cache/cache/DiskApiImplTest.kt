package org.master.cache.cache

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class DiskApiImplTest {

    @Test
    fun testGetFiles() {
        val resource = javaClass.classLoader.getResource("testData/getFilesTest").file
        val diskApi = DiskApiImpl(resource)
        val list = diskApi.getFiles().collectList()
        StepVerifier.create(list).consumeNextWith {
            assertNotNull(it)
            assertEquals("file.txt", it[0])
        }.verifyComplete()
    }

    @Test
    fun testReadFile() {
        val resource = javaClass.classLoader.getResource("testData/getFilesTest").file
        val diskApi = DiskApiImpl(resource)
        val readFile = diskApi.readFile("file.txt").collectList()
        StepVerifier.create(readFile).consumeNextWith {
            assertNotNull(it)
        }.verifyComplete()
    }

    @Test
    fun testWriteToFile() {
        val resource = javaClass.classLoader.getResource("testData/").file
        val diskApi = DiskApiImpl(resource)
        val writeFile = diskApi.writeFile("test.txt", "test".toByteArray()).flatMap {
            diskApi.readFile("test.txt").collectList()
        }
        StepVerifier.create(writeFile).consumeNextWith {
            assertNotNull(it)
            assertEquals("test", String(it.toByteArray()))
        }.verifyComplete()
    }
}