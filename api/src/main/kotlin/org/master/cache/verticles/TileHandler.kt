package org.master.cache.verticles

import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.RoutingContext
import org.apache.logging.log4j.LogManager
import org.master.cache.ClientPng
import org.master.cache.JsonMsgLabel
import org.master.cache.cache.DiskApi
import org.master.cache.cache.DiskApiImpl
import reactor.core.publisher.Mono

/**
 * Separate handler for tiles of map
 * @author kostya05983
 */
class TileHandler(startFuture: Future<Void>?, vertx: Vertx, config: JsonObject) : Handler<RoutingContext> {
    private var diskApi: DiskApi = DiskApiImpl(config.getString(JsonMsgLabel.CacheDir.name))
    private lateinit var currentListFiles: MutableList<String>
    private var client: ClientPng = ClientPng(vertx)
    private val logger = LogManager.getLogger(ProxyVerticle::class)


    init {
        diskApi.getFiles().collectList().subscribe({
            currentListFiles = it
            startFuture?.complete()
        }, {
            logger.error(it)
        })
    }

    override fun handle(event: RoutingContext) {
        val x = event.request().getParam(JsonMsgLabel.X.name)
        val y = event.request().getParam(JsonMsgLabel.Y.name)
        val z = event.request().getParam(JsonMsgLabel.Z.name)
        process(x, y, z).subscribe({
            event.response().end(Buffer.buffer(it))
        }, {
            logger.error(it)
        })
    }


    /**
     * Process the request from user
     * @return response in byteArray
     */
    private fun process(x: String, y: String, z: String): Mono<ByteArray> {
        val name = "$x$y$z"
        val list = currentListFiles.filter { it == name }
        return if (list.isEmpty()) {
            client.getResponse(x, y, z).map {
                it.body().bytes
            }.doOnSuccess {
                writeToDisk(name, it)
            }
        } else {
            diskApi.readFile(name).collectList().map { read ->
                read.toByteArray()
            }
        }
    }

    /**
     * Call diskApi for writing to disk in parallel
     * @param name - name of file without /
     * @param bytes - the image from openStreets map
     */
    private fun writeToDisk(name: String, bytes: ByteArray) {
        diskApi.writeFile(name, bytes).subscribe({
            currentListFiles.add(name)
        }, {
            logger.error(it)
        })
    }
}