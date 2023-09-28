package io.github.optijava.routes

import io.github.optijava.config.config
import io.github.optijava.core.BackendMetaData
import io.github.optijava.core.fileIndex
import io.github.optijava.core.totalUsedSize
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.registerBackendMetaDataRouting() {
    route("/api") {
         get("/meta") {
             call.respondText(Json.encodeToString(BackendMetaData(config, config.maxSize - totalUsedSize, fileIndex.size)))
         }
    }
}