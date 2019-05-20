package org.master.cache


import io.vertx.core.http.HttpMethod
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient

import org.apache.logging.log4j.LogManager
import org.master.cache.cache.DiskApiImpl
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.concurrent.ConcurrentHashMap

/**
 * Client for getting png images
 * @author kostya05983
 */
class ClientPng(vertx: Vertx) {
    companion object {
        private const val DOMAIN = "https://a.tile.openstreetmap.org"
    }

    private var client: WebClient = WebClient.create(vertx)

    private val currentRequestMemory: ConcurrentHashMap<String, Mono<HttpResponse<Buffer>>> = ConcurrentHashMap()

    fun getResponse(x: String, y: String, z: String): Mono<HttpResponse<Buffer>> {
        val name = "$x$y$z"
        return if (currentRequestMemory.containsKey(name)) {
            currentRequestMemory[name]!! //TODO retry request if failed
        } else {
            val mono = client.requestAbs(HttpMethod.GET, "$DOMAIN/$x/$y/$z")
                    .rxSend().toFlowable().doOnSubscribe {
                        currentRequestMemory.remove(name)
                    }.toMono()
            currentRequestMemory[name] = mono
            mono
        }
    }
}
