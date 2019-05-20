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
    /**
     * Get files from disk
     * @return - stream of file names after last /
     */
    override fun getFiles(): Flux<String> {
        return Flux.fromArray(File(path).listFiles()).map { it.name }
    }

    /**
     * @param name - name of file, such like this request http:://localhost:8080/2/4/5.png
     * the name of file will be path/245.png
     * @return stream of bytes
     */
    override fun readFile(name: String): Flux<Byte> {
        val stream = FileInputStream("$path/$name")
        return Flux.fromArray(stream.readBytes().toTypedArray()).doAfterTerminate {
            stream.close()
        }
    }

    /**
     * Write file to cache dir, close stream after terminate mono to avoid stream leaks
     * @param name - name of file, such like this request http:://localhost:8080/2/4/5.png
     * the name of file will be path/245.png
     * @param bytes - image
     * @return mono on action of writing to disk
     */
    override fun writeFile(name: String, bytes: ByteArray): Mono<Unit> {
        val file = File("$path/$name")
        file.createNewFile()
        val outStream = FileOutputStream(file)
        return Mono.fromCallable {
            outStream.write(bytes)
        }.doAfterTerminate {
            outStream.close()
        }
    }
}