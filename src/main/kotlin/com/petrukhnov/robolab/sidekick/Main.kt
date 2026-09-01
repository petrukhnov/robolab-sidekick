package com.petrukhnov.robolab.sidekick

import com.petrukhnov.robolab.sidekick.api.mcp.McpServer
import com.petrukhnov.robolab.sidekick.config.AppConfig
import com.petrukhnov.robolab.sidekick.infrastructure.serial.JSerialCommConnection
import com.petrukhnov.robolab.sidekick.infrastructure.serial.JSerialCommPortProvider
import com.petrukhnov.robolab.sidekick.service.LoggingService
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {
    if (args.contains("--mcp")) {
        startMcp()
    } else {
        println("RoboLab Sidekick v0.1.0")
        println("Usage: java -jar robolab-sidekick-all.jar [--mcp]")
        println("\nCurrently only MCP mode is supported via command line.")
        exitProcess(1)
    }
}

fun startMcp() {
    val config = AppConfig(serialPort = "AUTO")
    val loggingService = LoggingService(
        config = config,
        serialConnectionFactory = { port, baud -> JSerialCommConnection(port, baud) },
        portProvider = JSerialCommPortProvider()
    )
    
    val mcpServer = McpServer(loggingService)
    logger.info("Starting MCP Server on stdio...")
    mcpServer.start()
}
