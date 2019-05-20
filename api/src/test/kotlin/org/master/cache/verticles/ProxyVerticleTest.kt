package org.master.cache.verticles

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProxyVerticleTest {

    lateinit var client: WebClient

    @BeforeAll
    fun setUp(vertx: Vertx, context: VertxTestContext) {
        client = WebClient.create(vertx)

        vertx.deployVerticle(ProxyVerticle(), context.completing())
    }

    private fun sendRequest(context: VertxTestContext, checkPoint: Checkpoint, request: String) {
        client.requestAbs(HttpMethod.GET, request).send {
            if (it.failed()) {
                context.failNow(it.cause())
            } else {
                checkPoint.flag()
            }
        }
    }

    @Test
    fun testAllResponse(context: VertxTestContext) {
        val limit = 9

        val checkpoint = context.checkpoint(limit * limit * limit)
        val startTime = System.currentTimeMillis()
        for (x in 0 until limit) {
            for (y in 0 until limit) {
                for (z in 0 until limit) {
                    sendRequest(context, checkpoint, "http://localhost:8080/${3 + x}/$y/$z.png")
                }
            }
        }
        context.awaitCompletion(40000, TimeUnit.SECONDS)
        println("Time ${(System.currentTimeMillis() - startTime) / (limit * limit * limit)}")
    }

    @Test
    fun testParallelRequest(context: VertxTestContext) {
        val limit = 9

        val checkpoint = context.checkpoint(limit * limit * limit * 2)

        for (x in 0 until limit) {
            for (y in 0 until limit) {
                for (z in 0 until limit) {
                    Thread {
                        sendRequest(context, checkpoint, "http://localhost:8080/${3 + x}/$y/$z.png")
                    }.start()
                    Thread {
                        sendRequest(context, checkpoint, "http://localhost:8080/${3 + x}/$y/$z.png")
                    }.start()
                }
            }
        }
        context.awaitCompletion(60000, TimeUnit.SECONDS)
    }
}