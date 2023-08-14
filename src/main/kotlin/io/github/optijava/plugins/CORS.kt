package io.github.optijava.plugins;

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        allowNonSimpleContentTypes = true
    }
}
