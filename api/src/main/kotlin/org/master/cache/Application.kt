package org.master.cache

import io.vertx.core.Vertx
import org.master.cache.verticles.ProxyVerticle


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(ProxyVerticle())

}

