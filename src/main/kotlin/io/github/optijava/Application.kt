package io.github.optijava

import io.github.optijava.config.Config
import io.github.optijava.core.daemonThread
import io.github.optijava.core.rebuildFileIndex
import io.github.optijava.plugins.configureCORS
import io.github.optijava.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*
import org.slf4j.LoggerFactory

var logger: Logger = LoggerFactory.getLogger("Radium")

fun main(args: Array<String>) {
    Config().loadConfig(args)
    rebuildFileIndex()
    daemonThread.start()
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    logger.info("Configuring routes...")
    configureRouting()
    logger.info("Configuring CORS...")
    configureCORS()
}
