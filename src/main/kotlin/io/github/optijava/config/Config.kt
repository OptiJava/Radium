package io.github.optijava.config

import io.github.optijava.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.*

lateinit var config: Config

@Serializable
class Config(var storagePath: String, var expireTime: Long, var maxSize: Int)

fun loadConfig(args: Array<String>) {
    val dca1 = Path.of("./radium.json")

    if (args.isNotEmpty() && args[0].startsWith("config:")) {
        val configPath = Path.of(args[0].substring(8))
        if (configPath.exists()) {
            val content = configPath.readText()
            config = Json.decodeFromString<Config>(content)
            logger.info("Config file was loaded")
        } else {
            throw FileNotFoundException("Config path ${configPath.absolutePathString()} is not valid!")
        }
    } else if (dca1.exists()) {
        val content = dca1.readText()
        config = Json.decodeFromString<Config>(content)
        logger.info("Config file was loaded")
    } else {
        logger.warn("No config file was found, generating default config at ${dca1.absolutePathString()}...")
        try {
            config = Config(Path.of("./upload").absolutePathString(), 10080L /* unit: min, equals 7 days */, 10240 /* unit: MB, equals 10GB */)
            dca1.createFile()
            dca1.writeText(Json.encodeToString(config))
        } catch (e: IOException) {
            throw RuntimeException("Exception when generating default config file.", e)
        }
    }
}
