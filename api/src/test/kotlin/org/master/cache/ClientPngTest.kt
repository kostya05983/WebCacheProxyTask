package org.master.cache

import io.vertx.reactivex.core.Vertx
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class ClientPngTest {


    @Test
    fun text() {
        val vertx = Vertx.vertx()
        val clientPng = ClientPng(vertx)
        val test = clientPng.getResponse("1", "2", "3")
        StepVerifier.create(test).consumeNextWith {
            assertNotNull(it.body())
        }.verifyComplete()
    }
}