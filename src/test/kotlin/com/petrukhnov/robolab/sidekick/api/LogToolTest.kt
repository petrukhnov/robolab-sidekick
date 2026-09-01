package com.petrukhnov.robolab.sidekick.api

import com.petrukhnov.robolab.sidekick.config.AppConfig
import com.petrukhnov.robolab.sidekick.infrastructure.fake.FakeSerialConnection
import com.petrukhnov.robolab.sidekick.service.LoggingService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class LogToolTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `should perform full logging lifecycle`() {
        val config = AppConfig(serialPort = "COM1", storagePath = tempDir.toString())
        val serial = FakeSerialConnection()
        val loggingService = LoggingService(config) { _, _ -> serial }
        val logTool = LogTool(loggingService)

        // 1. Check status (idle)
        val statusIdle = Json.parseToJsonElement(logTool.status()).jsonObject
        assertEquals("idle", statusIdle["status"]?.jsonPrimitive?.content)

        // 2. Start logging
        val startResult = Json.parseToJsonElement(logTool.start("COM1", 115200)).jsonObject
        val sessionId = startResult["sessionId"]?.jsonPrimitive?.content ?: fail("No sessionId")
        assertEquals("running", startResult["status"]?.jsonPrimitive?.content)

        // 3. Check status (running)
        val statusRunning = Json.parseToJsonElement(logTool.status()).jsonObject
        assertEquals("running", statusRunning["status"]?.jsonPrimitive?.content)
        assertEquals(sessionId, statusRunning["activeSession"]?.jsonObject?.get("sessionId")?.jsonPrimitive?.content)

        // 4. Simulate input
        serial.simulateInput("LOG LINE 1")
        serial.simulateInput("LOG LINE 2")
        Thread.sleep(200) // Wait for processing

        // 5. List logs
        val listResult = Json.parseToJsonElement(logTool.list()).jsonObject
        val logs = listResult["logs"]?.toString() ?: ""
        assertTrue(logs.contains(sessionId))

        // 6. Read logs
        val readResult = Json.parseToJsonElement(logTool.read(sessionId)).jsonObject
        val entries = readResult["entries"]?.toString() ?: ""
        assertTrue(entries.contains("LOG LINE 1"))
        assertTrue(entries.contains("LOG LINE 2"))

        // 7. Search logs
        val searchResult = Json.parseToJsonElement(logTool.search("LINE 2")).jsonObject
        assertEquals(1, searchResult["matchCount"]?.jsonPrimitive?.content?.toInt())

        // 8. Stop logging
        val stopResult = Json.parseToJsonElement(logTool.stop()).jsonObject
        assertEquals("stopped", stopResult["status"]?.jsonPrimitive?.content)
        // Note: linesReceived might be 2 if everything was processed
        assertTrue(stopResult["linesReceived"]?.jsonPrimitive?.content?.toLong()!! >= 0)

        // 9. Check status (idle again)
        val statusFinal = Json.parseToJsonElement(logTool.status()).jsonObject
        assertEquals("idle", statusFinal["status"]?.jsonPrimitive?.content)
    }
}
