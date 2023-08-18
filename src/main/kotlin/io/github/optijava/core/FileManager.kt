package io.github.optijava.core

import io.github.optijava.config.config
import io.github.optijava.logger
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.*

var fileIndex: MutableMap<String, MetaData> = HashMap()

var storagePath: Path = Path.of(config.storagePath)

fun getNewFileID(fileName: String): String {
    return MessageDigest.getInstance("MD5")
        .digest((fileName + System.currentTimeMillis() + (1..44444444).random()).toByteArray())
        .joinToString("") { "%02x".format(it) }
}

fun rebuildFileIndex() {
    logger.info("Rebuilding file index...")

    if (!storagePath.exists()) {
        storagePath.createDirectory()
    }

    storagePath.listDirectoryEntries().forEach {
        if (it.isDirectory()) {
            var targetFileName = ""
            var metadataFileContent = ""
            it.listDirectoryEntries().forEach { pt ->
                if (pt.name != "info.json") {
                    targetFileName = pt.name
                } else {
                    metadataFileContent = pt.readText()
                }
            }

            check(targetFileName.isNotBlank()) { "Detected illegal folder: ${it.pathString}, please correct it or remove it" }

            val meta: MetaData = try {
                Json.decodeFromString<MetaData>(metadataFileContent)
            } catch (e: Exception) {
                logger.error("Exception when rebuilding file index, will skip this file, id: ${it.pathString}", e)
                return@forEach
            }

            UserFile(targetFileName, id = it.name, meta.uploadTime)
        }
    }

    logger.info("File index rebuild successfully!")
}

class UserFile(fileName: String, id: String = "", uploadTime: String) : MetaData(fileName, id, uploadTime) {
    init {
        if (id.isBlank()) {
            var fileID: String = getNewFileID(this.fileName)
            while (fileIndex.keys.contains(fileID)) {
                fileID = getNewFileID(fileName)
            }
            this.id = fileID
        }

        fileIndex[this.id] = this

        logger.info("Created user file instance ${this.fileName}, id: ${this.id}")
    }

    fun saveFile(streamProvider: InputStream) {
        val newFolderPath = storagePath.resolve(id)
        if (newFolderPath.exists()) throw IOException("Unexpected situation: File ${this.fileName}'s folder has already existed, id: $id")

        newFolderPath.createDirectory()

        val newFilePath = newFolderPath.resolve(fileName)

        newFilePath.createFile()
        newFilePath.writeBytes(streamProvider.use { it.readAllBytes() })

        val infoFilePath = newFolderPath.resolve("info.json")
        infoFilePath.createFile()
        infoFilePath.writeText(Json.encodeToString<MetaData>(this))

        logger.info("Successfully saved file $fileName, id: $id")
    }

    @OptIn(ExperimentalPathApi::class)
    fun removeFile() {
        storagePath.resolve(id).deleteRecursively()

        fileIndex.remove(id)
        logger.info("Successfully removed user file instance $fileName, id: $id")
    }
}
