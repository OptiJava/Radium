package io.github.optijava.core

import io.github.optijava.config.config
import io.github.optijava.core.exceptions.MaxSizeReachedExceptions
import io.github.optijava.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.io.path.*

var fileIndex = ConcurrentHashMap<String, MetaData>()

var storagePath: Path = Path.of(config.storagePath)

var totalUsedSize: Double = 0.0 /* unit: MB */
    set(value) {
        if (value < 0) {
            throw IllegalStateException("totalSize can't lower than 0.")
        }
        field = value
    }

@OptIn(ExperimentalPathApi::class)
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
                } else if (pt.name == "info.json") {
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

            try {
                UserFile(targetFileName, id = it.name, meta.uploadTime, size = it.fileSizeMB())
            } catch (mre: MaxSizeReachedExceptions) {
                logger.warn("Max size reached when reloading ${targetFileName}, remove remaining files!")
                it.deleteRecursively()
            }
        }
    }

    logger.info("File index rebuild successfully!")
}

fun updateTotalSizeAndAdd(a: Double) {
    if ((totalUsedSize + a) > config.maxSize) {
        throw MaxSizeReachedExceptions()
    }
    logger.info("Total size updated.")
    totalUsedSize += a
}

class UserFile(fileName: String, id: String = "", uploadTime: String, size: Double /* unit: MB */) :
    MetaData(fileName, id, uploadTime, size) {
    init {
        updateTotalSizeAndAdd(size)

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
        try {
            updateTotalSizeAndAdd(-size)
        } catch (_: Throwable) {
        }

        storagePath.resolve(id).deleteRecursively()

        fileIndex.remove(id)
        logger.info("Successfully removed user file instance $fileName, id: $id")
    }
}

val daemonThread = thread(isDaemon = true, start = false, name = "Radium Daemon") {
    while (!Thread.currentThread().isInterrupted) {
        for (meta in fileIndex.values) {
            if (config.expireTime > 0 && isExpired(meta.uploadTime, config.expireTime)) {
                logger.info("${meta.fileName} was expired, id: ${meta.id}")
                (meta as UserFile).removeFile()
            }
        }
    }
}

@Serializable
open class MetaData(var fileName: String, var id: String = "", var uploadTime: String, var size: Double /* unit: MB */)
