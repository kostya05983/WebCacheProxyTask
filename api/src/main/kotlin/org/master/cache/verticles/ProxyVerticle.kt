package org.master.cache.verticles

import io.vertx.core.Future
import io.vertx.reactivex.ext.web.Router
import org.master.cache.JsonMsgLabel

/**
 * Proxy verticle for proxy request to openStreet map
 * @author kostya05983
 */
class ProxyVerticle : io.vertx.reactivex.core.AbstractVerticle() {
    private lateinit var tileHandler: TileHandler


    override fun start(startFuture: Future<Void>?) {
        tileHandler = TileHandler(startFuture, vertx, config())
        setUpRouter()
    }

    /**
     * Set Up router to proxy Request
     */
    private fun setUpRouter() {
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)
        router.route("/:X/:Y/:Z").handler(tileHandler)
        server.requestHandler(router::handle).listen(config().getInteger(JsonMsgLabel.Port.name))
    }

}