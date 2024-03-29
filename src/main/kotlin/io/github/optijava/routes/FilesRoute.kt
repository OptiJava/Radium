package io.github.optijava.routes

import io.github.optijava.core.PrivateUserFile
import io.github.optijava.core.UserFile
import io.github.optijava.core.exceptions.MaxSizeReachedExceptions
import io.github.optijava.core.fileIndex
import io.github.optijava.core.storagePath
import io.github.optijava.logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Route.registerFilesRouting() {
    route("/api") {
        get {
            call.respondRedirect("/api/files", true)
        }
        route("/files") {
            /////////////////////////////
            //        file list        //
            /////////////////////////////
            get {
                call.respondRedirect("/api/files/list", true)
            }
            get("/list") {
                call.respondText(
                    Json.encodeToString(fileIndex.values.toList().filter { it !is PrivateUserFile }),
                    ContentType.Text.Plain,
                    status = HttpStatusCode.OK
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
                call.respondRedirect("/api/files/${call.parameters["id"]}/", permanent = true)
            }
            get("/{id?}/{filename?}") {
                if (!fileIndex.containsKey(call.parameters["id"])) {
                    call.respondText("404 Not Found", status = HttpStatusCode.NotFound)
                    return@get
                }

                call.respondFile(
                    storagePath.resolve(call.parameters["id"]!!).resolve(fileIndex[call.parameters["id"]!!]!!.fileName)
                        .toFile()
                )
            }
            get("/{id?}/") {
                if (!fileIndex.containsKey(call.parameters["id"])) {
                    call.respondText("404 Not Found", status = HttpStatusCode.NotFound)
                    return@get
                }
                call.respondText(
                    Json.encodeToString(fileIndex[call.parameters["id"]!!]!!),
                    status = HttpStatusCode.OK
                )
            }
            ////////////////////////////
            //      file remove       //
            ////////////////////////////
            delete("/{id?}/") {
                if (!fileIndex.containsKey(call.parameters["id"])) {
                    call.respondText("404 Not Found", status = HttpStatusCode.NotFound)
                    return@delete
                }
                try {
                    ((fileIndex[call.parameters["id"]]!!) as UserFile).removeFile()
                    call.respondText(call.parameters["id"]!!, status = HttpStatusCode.OK)
                } catch (e: Throwable) {
                    call.respondText("Exception when deleting file $e", status = HttpStatusCode.InternalServerError)
                    logger.error("Exception when deleting file", e)
                }
            }
        }

        route("/upload") {
            //////////////////////////////
            //       file upload        //
            //////////////////////////////
            put("/{filename?}") {
                logger.info("Uploading files...")
                withContext(Dispatchers.IO) {
                    var userFile: UserFile? = null
                    try {
                        userFile = UserFile(
                            call.parameters["filename"]!!,
                            uploadTime = (LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(
                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                            )),
                            size = try {
                                call.request.headers["Content-Length"]!!.toLong().div(1024.0 * 1024.0)
                            } catch (e: NullPointerException) {
                                call.respondText(
                                    "You must provide file size in Content-Length.",
                                    ContentType.Text.Plain,
                                    status = HttpStatusCode.BadRequest
                                )
                                return@withContext
                            }
                        )
                        userFile.saveFile(streamProvider = call.receiveStream())
                        call.respondText(userFile.id, status = HttpStatusCode.OK)
                    } catch (mre: MaxSizeReachedExceptions) {
                        logger.error("=========Max size reached!!!=========", mre)
                        call.respondText(
                            "Exception when handle put request at ${call.url()}, max size has already reached",
                            status = HttpStatusCode.InternalServerError
                        )
                    } catch (e: Throwable) {
                        try {
                            userFile?.removeFile()
                        } catch (e: Throwable) {
                            logger.error("Exception when removing invalid file", e)
                        } finally {
                            logger.error(
                                "Exception when handle put request at ${call.url()}, the invalid file was removed",
                                e
                            )
                            call.respondText(
                                "Exception when handle put request at ${call.url()}, the invalid file was removed",
                                status = HttpStatusCode.InternalServerError
                            )
                        }
                    }
                }
            }

            get("/{filename?}") {
                call.respondText("400 Bad Request. \nPlease use PUT request here.", status = HttpStatusCode.BadRequest)
            }
        }
    }
}
