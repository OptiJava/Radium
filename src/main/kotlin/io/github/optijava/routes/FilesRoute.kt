package io.github.optijava.routes

import io.github.optijava.core.UserFile
import io.github.optijava.core.fileIndex
import io.github.optijava.core.getFormattedTimeNow
import io.github.optijava.core.storagePath
import io.github.optijava.logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.registerFilesRouting() {
    route("/api/files") {
        /////////////////////////////
        //        file list        //
        /////////////////////////////
        get {
            call.respondRedirect("/api/files/list", true)
        }
        get("/list") {
            call.respondText(
                "$fileIndex\n\nThis page has not been implemented yet.",
                ContentType.Text.Plain,
                status = HttpStatusCode.NotImplemented
            )
        }

        //////////////////////////////
        //      file download       //
        //////////////////////////////
        get("/{id?}") {
            if (call.parameters["id"] == null) {
                call.respondText("404 Not Found.", status = HttpStatusCode.NotFound)
                return@get
            }
            call.respondRedirect("/api/files/${call.parameters["id"]}/info", permanent = true)
        }
        get("/{id?}/{filename?}") {
            if (!fileIndex.contains(call.parameters["id"])) {
                call.respondText("404 Not Found", status = HttpStatusCode.NotFound)
                return@get
            }

            call.respondFile(
                storagePath.resolve(call.parameters["id"]!!).resolve(fileIndex[call.parameters["id"]!!]!!.fileName)
                    .toFile()
            )
        }
        get("/{id?}/info") {
            if (!fileIndex.contains(call.parameters["id"])) {
                call.respondText("404 Not Found", status = HttpStatusCode.NotFound)
                return@get
            }
            call.respondText(
                "${Json.encodeToString(fileIndex[call.parameters["id"]!!]!!)} \n\nThis page has not been implemented yet.",
                status = HttpStatusCode.NotImplemented
            )
        }
    }


    route("/api/upload") {
        //////////////////////////////
        //       file upload        //
        //////////////////////////////
        put("/{filename?}") {
            logger.info("Uploading files...")
            try {
                val userFile = UserFile(call.parameters["filename"]!!, uploadTime = getFormattedTimeNow())
                userFile.saveFile(streamProvider = call.receiveStream())
                call.respondText(userFile.id, status = HttpStatusCode.OK)
            } catch (e: Throwable) {
                logger.error("Exception when handle put request at ${call.url()}", e)
                call.respondText(
                    "Exception when handle put request at ${call.url()}",
                    status = HttpStatusCode.InternalServerError
                )
                return@put
            }
        }

        get("/{filename?}") {
            call.respondText("400 Bad Request. \nPlease use PUT request here.", status = HttpStatusCode.BadRequest)
        }
    }
}
