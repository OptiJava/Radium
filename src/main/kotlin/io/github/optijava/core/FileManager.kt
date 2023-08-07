package io.github.optijava.core

import io.github.optijava.config.config
import io.github.optijava.logger
import java.io.InputStream
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.*

var fileIndex: MutableMap<String, UserFile> = HashMap()

var storagePath = Path.of(config.storagePath)

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
            it.listDirectoryEntries().forEach { pt ->
                if (pt.name != "info.json") {
                    targetFileName = pt.name
                }
            }
            if (targetFileName.isBlank()) {
                logger.error("Detected illegal folder: ${it.pathString}")
                return
            }
            PublicUserFile(targetFileName, it.name)
        }
    }

    logger.info("File index rebuild successfully!")
}

open class UserFile(var fileName: String, var id: String = "") {
    init {
        if (id.isBlank()) {
            var fileID: String = getNewFileID(this.fileName)
            while (fileIndex.keys.contains(fileID)) {
                fileID = getNewFileID(fileName)
            }
            this.id = fileID
        }

        if (!storagePath.exists()) {
            storagePath.createDirectory()
        }

        fileIndex[this.id] = this

        logger.info("Created user file instance $fileName, id: $id")
    }

    fun saveFile(streamProvider: InputStream) {
        storagePath.resolve(id).createDirectory()

        val newFilePath = storagePath.resolve(id).resolve(fileName)

        newFilePath.createFile()
        newFilePath.writeBytes(streamProvider.use { it.readAllBytes() })

        logger.info("Successfully saved file $fileName, id: $id")
    }

    @OptIn(ExperimentalPathApi::class)
    fun removeFile() {
        storagePath.resolve(id).deleteRecursively()

        fileIndex.remove(id)
        logger.info("Successfully removed user file instance $fileName, id: $id")
    }
}

class PublicUserFile(publicFileName: String, publicFileID: String = "") : UserFile(publicFileName, publicFileID)
