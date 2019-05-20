package org.master.cache.cache

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DiskApi {

    fun getFiles(): Flux<String>

    fun readFile(name: String): Flux<Byte>

    fun writeFile(name: String, bytes: ByteArray): Mono<Unit>
}