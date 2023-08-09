package io.github.optijava.plugins

import io.github.optijava.routes.registerFilesRouting
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        staticResources("/", "static")
        registerFilesRouting()
    }
}