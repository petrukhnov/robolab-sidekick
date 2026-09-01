package com.petrukhnov.robolab.sidekick

import com.petrukhnov.robolab.sidekick.api.LogTool
import com.petrukhnov.robolab.sidekick.config.AppConfig
import com.petrukhnov.robolab.sidekick.infrastructure.serial.JSerialCommConnection
import com.petrukhnov.robolab.sidekick.infrastructure.serial.JSerialCommPortProvider
import com.petrukhnov.robolab.sidekick.service.LoggingService
import com.fazecast.jSerialComm.SerialPort
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val portName = args.getOrNull(0) ?: "/dev/ttyUSB0"
    val baudRate = args.getOrNull(1)?.toIntOrNull() ?: 115200

    if (args.isEmpty()) {
        val ports = SerialPort.getCommPorts()
        val usb0Exists = ports.any { it.systemPortName == "/dev/ttyUSB0" } || java.io.File("/dev/ttyUSB0").exists()
        
        if (!usb0Exists) {
            println("Usage: ./gradlew run --args=\"<port> [baudRate]\"")
            println("Available ports:")
            if (ports.isEmpty()) {
                println("  No serial ports found.")
            } else {
                ports.forEach {
                    println("  - ${it.systemPortName} (${it.descriptivePortName})")
                }
            }
            exitProcess(0)
        } else {
            println("Using default port /dev/ttyUSB0. To override, use: ./gradlew run --args=\"<port> [baudRate]\"")
        }
    }

    val config = AppConfig(serialPort = portName, baudRate = baudRate, storagePath = "manual-logs")
    val loggingService = LoggingService(config, { p, b -> JSerialCommConnection(p, b) }, JSerialCommPortProvider())
    val logTool = LogTool(loggingService)

    println("Starting logging on $portName at $baudRate baud...")
    val startResult = logTool.start(portName, baudRate)
    println("Start result: $startResult")

    if (startResult.contains("error")) {
        println("Failed to start logging. Exiting.")
        exitProcess(1)
    }

    println("Logging started. Running for 7 seconds...")
    Thread.sleep(7000)

    println("Stopping logging...")
    val stopResult = loggingService.stop()
    println("Stop result: Session ${stopResult.sessionId}, lines received: ${stopResult.linesReceived}")
    
    if (stopResult.linesReceived >= 10) {
        println("SUCCESS: Received ${stopResult.linesReceived} messages (>= 10).")
    } else {
        println("FAILURE: Received only ${stopResult.linesReceived} messages (< 10).")
        exitProcess(1)
    }
    
    println("Logs saved to manual-logs directory.")
}
