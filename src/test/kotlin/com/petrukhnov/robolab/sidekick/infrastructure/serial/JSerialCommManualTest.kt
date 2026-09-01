package com.petrukhnov.robolab.sidekick.infrastructure.serial

import com.petrukhnov.robolab.sidekick.config.AppConfig
import com.petrukhnov.robolab.sidekick.service.SerialLogger
import com.petrukhnov.robolab.sidekick.infrastructure.fake.FakeLogWriter
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class JSerialCommManualTest {

    @Test
    fun listPorts() {
        val provider = JSerialCommPortProvider()
        val ports = provider.listPorts()
        println("Available ports:")
        if (ports.isEmpty()) {
            println("  None found.")
        } else {
            ports.forEach {
                println("  - ${it.systemPortName} (${it.descriptivePortName}): ${it.portDescription} [Open: ${it.isOpen}]")
            }
        }
    }

    @Test
    @Disabled("Requires real hardware (NodeMCU) connected to a serial port")
    fun testRealHardwareLogging() {
        val portName = System.getProperty("serial.port") ?: "/dev/ttyUSB0"
        val baudRate = System.getProperty("serial.baud")?.toIntOrNull() ?: 115200
        
        println("Connecting to $portName at $baudRate baud...")
        
        val connection = JSerialCommConnection(portName, baudRate)
        val writer = FakeLogWriter() // Just for testing
        val config = AppConfig(serialPort = portName, baudRate = baudRate)
        
        val logger = SerialLogger(connection, writer, config)
        
        try {
            logger.start()
            println("Logging started. Printing lines for 10 seconds...")
            
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 10000) {
                val entries = writer.entries
                if (entries.isNotEmpty()) {
                    entries.forEach { println("LOG: ${it.rawMessage}") }
                    entries.clear()
                }
                Thread.sleep(1000)
            }
        } finally {
            logger.stop()
            println("Stopped.")
        }
    }
}
