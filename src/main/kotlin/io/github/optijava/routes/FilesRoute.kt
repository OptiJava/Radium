package io.github.optijava.routes

import io.github.optijava.core.PublicUserFile
import io.github.optijava.core.fileIndex
import io.github.optijava.core.storagePath
import io.github.optijava.logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.registerFilesRouting() {
    route("/files") {
        /////////////////////////////
        //        file list        //
        /////////////////////////////
        get {
            call.respondRedirect("/files/list", true)
        }
        get("/list") {
            call.respondText("$fileIndex\n\nThis page has not been implemented yet.", ContentType.Text.Plain, status = HttpStatusCode.NotImplemented)
        }

        //////////////////////////////
        //      file download       //
        //////////////////////////////
        get("/{id?}") {
            call.respondRedirect("/files/${call.parameters["id"]}/info", permanent = true)
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
            call.respondText("This page has not been implemented yet.", status = HttpStatusCode.NotImplemented)
        }
    }


    route("/upload") {
        //////////////////////////////
        //       file upload        //
        //////////////////////////////
        put("/{filename?}") {
            logger.info("Uploading files...")
            try {
                PublicUserFile(call.parameters["filename"]!!).saveFile(streamProvider = call.receiveStream())
            } catch (e: Throwable) {
                logger.error("Exception when handle put request at ${call.url()}", e)
                call.respondText(
                    "Exception when handle put request at ${call.url()}",
                    status = HttpStatusCode.InternalServerError
                )
                return@put
            }
            call.respondText("Success", status = HttpStatusCode.OK)
        }

        get("/{filename?}") {
            call.respondText("400 Bad Request. \nPlease use PUT request here.", status = HttpStatusCode.BadRequest)
        }
    }
}
