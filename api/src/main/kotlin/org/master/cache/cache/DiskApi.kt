package org.master.cache.cache

import reactor.core.publisher.Flux

interface DiskApi {

    fun getFiles(): Flux<String>

    fun readFile(name: String): Flux<Byte>

    fun writeFile(name: String, bytes: ByteArray)
}