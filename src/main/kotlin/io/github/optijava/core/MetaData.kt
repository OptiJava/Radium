package io.github.optijava.core

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
open class MetaData(var fileName: String, var id: String = "", var uploadTime: String)

fun getFormattedTimeNow(): String {
    return LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
}
