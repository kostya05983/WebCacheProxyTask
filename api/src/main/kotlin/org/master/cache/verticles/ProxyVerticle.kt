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

        val server = vertx.createHttpServer()
        val router = Router.router(vertx)
        router.route("/:x/:y/:z").handler { context ->
            val x = context.request().getParam("x")
            val y = context.request().getParam("y")
            val z = context.request().getParam("z")
            process(x, y, z).subscribe({
                context.response().end(Buffer.buffer(it))
            }, {
                logger.error(it)
            })
        }
        server.requestHandler(router::handle).listen(config().getInteger(JsonMsgLabel.Port.name))
    }

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

    private fun writeToDisk(name: String, bytes: ByteArray) {
        diskApi.writeFile(name, bytes).subscribe({
            currentListFiles.add(name)
        }, {
            logger.error(it)
        })
    }
}