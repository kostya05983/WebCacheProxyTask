package org.master.cache.verticles

import io.vertx.core.Future
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import org.apache.logging.log4j.LogManager
import org.master.cache.ClientPng
import org.master.cache.JsonMsgLabel
import org.master.cache.cache.DiskApi
import org.master.cache.cache.DiskApiImpl
import reactor.core.publisher.Mono

/**
 * Proxy verticle for proxy request to openStreet map
 * @author kostya05983
 */
class ProxyVerticle : io.vertx.reactivex.core.AbstractVerticle() {
    private lateinit var diskApi: DiskApi
    private val logger = LogManager.getLogger(ProxyVerticle::class)

    private lateinit var client: ClientPng
    private lateinit var currentListFiles: MutableList<String>

    override fun start(startFuture: Future<Void>?) {
        client = ClientPng(vertx)
        diskApi = DiskApiImpl(config().getString(JsonMsgLabel.CacheDir.name))

        diskApi.getFiles().collectList().subscribe({
            currentListFiles = it
            startFuture?.complete()
        }, {
            logger.error(it)
        })

        setUpRouter()
    }

    /**
     * Set Up router to proxy Request
     */
    private fun setUpRouter() {
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)
        router.route("/:X/:Y/:Z").handler { context ->
            val x = context.request().getParam(JsonMsgLabel.X.name)
            val y = context.request().getParam(JsonMsgLabel.Y.name)
            val z = context.request().getParam(JsonMsgLabel.Z.name)
            process(x, y, z).subscribe({
                context.response().end(Buffer.buffer(it))
            }, {
                logger.error(it)
            })
        }
        server.requestHandler(router::handle).listen(config().getInteger(JsonMsgLabel.Port.name))
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