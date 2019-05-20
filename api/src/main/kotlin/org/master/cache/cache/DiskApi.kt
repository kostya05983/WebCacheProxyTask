package org.master.cache.cache

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DiskApi {

    /**
     * Get files from disk
     * @return - stream of file names after last /
     */
    fun getFiles(): Flux<String>

    /**
     * @param name - name of file, such like this request http:://localhost:8080/2/4/5.png
     * the name of file will be path/245.png
     * @return stream of bytes
     */
    fun readFile(name: String): Flux<Byte>

    /**
     * Write file to cache dir, close stream after terminate mono to avoid stream leaks
     * @param name - name of file, such like this request http:://localhost:8080/2/4/5.png
     * the name of file will be path/245.png
     * @param bytes - image
     * @return mono on action of writing to disk
     */
    fun writeFile(name: String, bytes: ByteArray): Mono<Unit>
}