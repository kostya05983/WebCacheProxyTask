package org.master.cache.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient
import org.apache.logging.log4j.LogManager
import org.master.cache.cache.DiskApi
import org.master.cache.cache.DiskApiImpl
import java.util.concurrent.ConcurrentHashMap

class ProxyVerticle : AbstractVerticle() {
    lateinit var diskApi: DiskApi
    lateinit var client: WebClient
    val logger = LogManager.getLogger(ProxyVerticle::class)


    override fun start() {
        super.start()
        client = WebClient.create(vertx)
        diskApi = DiskApiImpl()
        val server = vertx.createHttpServer()

        val router = Router.router(vertx)

        val handler = router.route("/:x/:y/:z").handler {
            val x = it.request().getParam("x")
            val y = it.request().getParam("y")
            val z = it.request().getParam("z")
            process(it, "$x/$y/$z")
        }

        server.requestHandler(router::handle).listen(8080)
    }

    fun process(context: RoutingContext, name: String) {
        val list = diskApi.getFiles().filter {
            it == name
        }.collectList()

        list.subscribe({
            if (it.isEmpty()) {
                getResponse(context, name)
            } else {
                diskApi.readFile(name).collectList().map { list ->
                    list.toByteArray()
                }.subscribe({
                    context.response().end(io.vertx.core.buffer.Buffer.buffer(it))
                }, {
                    logger.error(it)
                })
            }
        }, {
            logger.error(it)
        })
    }

    private val currentRequestMemory: ConcurrentHashMap<String, Future<ByteArray>> = ConcurrentHashMap()

    fun getResponse(context: RoutingContext, xyz: String) {
        if (currentRequestMemory.contains(xyz)) {
            val deferred = currentRequestMemory[xyz]
            deferred?.setHandler {
                if (it.succeeded()) {
                    val request = context.response()
                    request.end(io.vertx.core.buffer.Buffer.buffer(it.result()))
                } else {
                    logger.error(it.cause())
                }
            }
        } else {
            val future = Future.future<ByteArray>()
            currentRequestMemory[xyz] = future
            client.requestAbs(HttpMethod.GET, "https://a.tile.openstreetmap.org/$xyz").send {
                if (it.succeeded()) {
                    val bytes = it.result().body().bytes
                    future.complete(bytes)
                    context.response().end(io.vertx.core.buffer.Buffer.buffer(bytes))
                    currentRequestMemory.remove(xyz)
                } else {
                    logger.error(it.cause())
                }
            }
        }
    }


}