package com.petrukhnov.robolab.sidekick.service

import com.petrukhnov.robolab.sidekick.config.AppConfig
import com.petrukhnov.robolab.sidekick.infrastructure.fake.FakeLogWriter
import com.petrukhnov.robolab.sidekick.infrastructure.fake.FakeSerialConnection
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class SerialLoggerTest {

    @Test
    fun `should capture serial lines and write to log`() {
        // Given
        val serial = FakeSerialConnection()
        val writer = FakeLogWriter()
        val config = AppConfig(serialPort = "COM1")
        val fixedClock = Clock.fixed(Instant.parse("2026-08-31T20:41:13.245Z"), ZoneId.of("UTC"))
        val logger = SerialLogger(serial, writer, config, fixedClock)

        // When
        logger.start()
        serial.simulateInput("MOTOR LEFT CURRENT: 4.82A")
        serial.simulateInput("MOTOR RIGHT CURRENT: 4.75A")
        
        // Give it some time to process
        Thread.sleep(100)
        logger.stop()

        // Then
        assertEquals(2, writer.entries.size)
        
        val firstEntry = writer.entries[0]
        assertEquals(1L, firstEntry.sequence)
        assertEquals("arduino", firstEntry.source)
        assertEquals("MOTOR LEFT CURRENT: 4.82A", firstEntry.rawMessage)
        assertEquals(Instant.parse("2026-08-31T20:41:13.245Z"), firstEntry.timestamp)

        val secondEntry = writer.entries[1]
        assertEquals(2L, secondEntry.sequence)
        assertEquals("MOTOR RIGHT CURRENT: 4.75A", secondEntry.rawMessage)
    }

    @Test
    fun `should throw exception when starting twice`() {
        val serial = FakeSerialConnection()
        val writer = FakeLogWriter()
        val config = AppConfig(serialPort = "COM1")
        val logger = SerialLogger(serial, writer, config)

        logger.start()
        assertThrows(IllegalStateException::class.java) {
            logger.start()
        }
        logger.stop()
    }
}
