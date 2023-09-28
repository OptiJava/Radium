package io.github.optijava.core

import io.github.optijava.config.Config
import kotlinx.serialization.Serializable
import java.nio.file.Path
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.fileSize

fun getNewFileID(fileName: String): String {
    return MessageDigest.getInstance("MD5")
        .digest((fileName + System.currentTimeMillis() + (1..44444444).random()).toByteArray())
        .joinToString("") { "%02x".format(it) }
}

fun isExpired(createTime: String, min: Long): Boolean {
    val createDateTime = LocalDateTime.parse(createTime, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
    val now = LocalDateTime.now()

    val diffInSeconds = java.time.Duration.between(createDateTime, now).seconds

    return diffInSeconds >= (min * 60)
}

fun Path.fileSizeMB(): Double = this.fileSize().div(1024.0 * 1024.0)

@Serializable
class BackendMetaData(var config: Config, var freeSpace: Double, var fileCount: Int)