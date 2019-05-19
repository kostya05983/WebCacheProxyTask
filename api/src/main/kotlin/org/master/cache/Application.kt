package org.master.cache

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.master.cache.controller.MapPartController


fun main(args: Array<String>) {
    val controller = MapPartController()
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {

                call.respondText("Hello World!", ContentType.Text.Plain)
            }
        }
    }
    server.start(wait = true)
}