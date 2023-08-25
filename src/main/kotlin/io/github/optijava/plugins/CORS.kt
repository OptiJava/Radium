package io.github.optijava.plugins;

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        anyHost()

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)

        allowHeaders { true }

        allowNonSimpleContentTypes = true
    }
}
