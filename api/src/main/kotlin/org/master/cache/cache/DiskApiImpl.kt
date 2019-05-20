package org.master.cache.cache

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Api for communicating with disk
 * @author kostya05983
 */
class DiskApiImpl(private val path: String) : DiskApi {
    override fun getFiles(): Flux<String> {
        return Flux.fromArray(File(path).listFiles()).map { it.name }
    }

    override fun readFile(name: String): Flux<Byte> {
        val stream = FileInputStream("$path/$name")
        val flux = Flux.fromArray(stream.readBytes().toTypedArray())
        stream.close()
        return flux
    }

    override fun writeFile(name: String, bytes: ByteArray): Mono<Unit> {
        val file = File("$path/$name")
        file.createNewFile()
        val outStream = FileOutputStream(file)
        return Mono.fromCallable {
            outStream.write(bytes)
        }
    }
}