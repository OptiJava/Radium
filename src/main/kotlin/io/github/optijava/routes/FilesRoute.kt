package io.github.optijava.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.registerFilesRouting() {
    route("/files") {
        /////////////////////////////
        //        file list        //
        /////////////////////////////
        get {
            call.respondRedirect("/files/list", false)
        }
        get("/list") {
            call.respondText("Nothing", ContentType.Text.Plain, status = HttpStatusCode.NotImplemented)
        }

        //////////////////////////////
        //      file download       //
        //////////////////////////////
        get("/{id?}") {
            call.respondRedirect("/files/${call.parameters["id"]}/info")
        }
        get("/{id?}/{filename?}") {
            call.respondFile(File("./build.gradle.kts"))
        }
        get("/{id?}/info") {
            call.respondText("This page is not complete.", status = HttpStatusCode.NotImplemented)
        }
    }
}