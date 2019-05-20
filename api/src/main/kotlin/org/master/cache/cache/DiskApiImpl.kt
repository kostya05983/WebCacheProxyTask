package org.master.cache.cache

import reactor.core.publisher.Flux
import java.io.File
import java.io.FileInputStream

class DiskApiImpl : DiskApi {
    private companion object {
        private const val PATH_DIR = "/home/kostya05983/cacheImages"
    }

    override fun getFiles(): Flux<String> {
        return Flux.fromArray(File(PATH_DIR).listFiles()).map { it.name }
    }

    override fun readFile(name: String): Flux<Byte> {
        val stream = FileInputStream("$PATH_DIR/$name")
        return Flux.fromArray(stream.readAllBytes().toTypedArray())
    }

    override fun writeFile(name: String, bytes: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}