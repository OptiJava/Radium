package io.github.optijava.plugins

import io.github.optijava.routes.registerFilesRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("TODO", ContentType.Text.Plain)
        }
        registerFilesRouting()
    }
}