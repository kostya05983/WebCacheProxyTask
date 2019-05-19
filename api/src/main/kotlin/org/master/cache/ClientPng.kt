package org.master.cache

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.ConcurrentHashMap

class ClientPng {

    companion object {
        private const val SOCKET_TIMEOUT = 10_000
        private const val CONNECT_TIMEOUT = 10_000
        private const val CONNECTION_REQUEST_TIMEOUT = 20_000
    }

    lateinit var client: HttpClient

    init {
        client = HttpClient(Apache) {
            engine {
                socketTimeout = SOCKET_TIMEOUT
                connectTimeout = CONNECT_TIMEOUT
                connectionRequestTimeout = CONNECTION_REQUEST_TIMEOUT
            }
        }
    }

    private val currentRequestMemory: ConcurrentHashMap<String, Deferred<ByteArray>> = ConcurrentHashMap()

    suspend fun get(xyz: String): ByteArray {
        if (currentRequestMemory.contains(xyz)) {
            val deferred = currentRequestMemory[xyz]
            val bytes = deferred?.await() //TODO retry if request failed
            return bytes!!
        } else {
            currentRequestMemory[xyz] = GlobalScope.async {
                client.get<ByteArray>("https://a.tile.openstreetmap.org/xyz")
            }
            val bytes = currentRequestMemory[xyz]!!.await()
            currentRequestMemory.remove(xyz)
            return bytes
        }
    }
}