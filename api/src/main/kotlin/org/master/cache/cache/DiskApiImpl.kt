package org.master.cache.cache

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DiskApiImpl : DiskApi {
    private companion object {
        private const val PATH_DIR = "/home/kostya05983/cacheImages"
        private const val DOT_EXTENSION = "."
    }

    override fun getFiles(): Flux<String> {
        return Flux.fromArray(File(PATH_DIR).listFiles()).map { it.name.substring(0, it.name.indexOf(DOT_EXTENSION)) }
    }

    override fun readFile(name: String): Flux<Byte> {
        val stream = FileInputStream("$PATH_DIR/$name")

        return Flux.fromArray(stream.readBytes().toTypedArray())
    }

    override fun writeFile(name: String, bytes: ByteArray): Mono<Unit> {
        val outStream = FileOutputStream("$PATH_DIR/$name.png")
        return Mono.fromCallable {
            outStream.write(bytes)
        }
    }
}