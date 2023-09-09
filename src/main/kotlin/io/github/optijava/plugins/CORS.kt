package io.github.optijava.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        anyHost()

        for (m in HttpMethod.DefaultMethods) {
            allowMethod(m)
        }

        allowHeaders { true }

        allowNonSimpleContentTypes = true
    }
}
