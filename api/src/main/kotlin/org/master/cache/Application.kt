package org.master.cache

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.master.cache.verticles.ProxyVerticle
import java.nio.file.Files
import java.nio.file.Paths


fun main() {
    val vertx = Vertx.vertx()

    val options = DeploymentOptions()
    val config = loadConfig()
    options.config = config
    vertx.deployVerticle(ProxyVerticle(), options)
}

fun loadConfig(): JsonObject {
    return JsonObject(Files.readAllLines(Paths.get("/etc/proxyCache/config.json")).joinToString(""))
}

