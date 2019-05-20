package org.master.cache


import io.vertx.rxjava.core.Vertx
import io.vertx.rxjava.core.buffer.Buffer
import io.vertx.rxjava.ext.web.client.HttpResponse
import io.vertx.rxjava.ext.web.client.WebClient
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import reactor.core.publisher.toMono
import rx.Single
import java.util.concurrent.ConcurrentHashMap

class ClientPng(val vertx: Vertx) {

    companion object {
        private const val SOCKET_TIMEOUT = 10_000
        private const val CONNECT_TIMEOUT = 10_000
        private const val CONNECTION_REQUEST_TIMEOUT = 20_000
    }

    lateinit var client: WebClient

    init {
        client = WebClient.create(vertx)
    }

    private val currentRequestMemory: ConcurrentHashMap<String, Deferred<ByteArray>> = ConcurrentHashMap()

//    suspend fun get(xyz: String):  {
//        return if (currentRequestMemory.contains(xyz)) {
//            val deferred = currentRequestMemory[xyz]
//            val bytes = deferred?.await() //TODO retry if request failed
//            bytes!!
//        } else {
//            val rxSend: Single<HttpResponse<Buffer>> = client.get("https://a.tile.openstreetmap.org/$xyz").rxSend()
//
//            rxSend
//        }
//
//        val bytes = currentRequestMemory[xyz]!!.await()
//        currentRequestMemory.remove(xyz)
//        bytes
//    }
}
