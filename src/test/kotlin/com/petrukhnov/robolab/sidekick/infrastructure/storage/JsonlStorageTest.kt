package com.petrukhnov.robolab.sidekick.infrastructure.storage

import com.petrukhnov.robolab.sidekick.domain.LogEntry
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.time.Instant

class JsonlStorageTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `should write and read logs`() {
        val logFile = File(tempDir, "test.jsonl")
        val writer = JsonlLogWriter(logFile)
        val entry = LogEntry(
            timestamp = Instant.parse("2026-08-31T20:41:13.245Z"),
            sequence = 1,
            source = "arduino",
            rawMessage = "test message"
        )

        writer.append(entry)
        writer.flush()
        writer.close()

        val reader = JsonlLogReader(tempDir)
        val logs = reader.listLogs()
        assertEquals(listOf("test.jsonl"), logs)

        val entries = reader.readLog("test.jsonl")
        assertEquals(1, entries.size)
        assertEquals(entry, entries[0])
    }

    @Test
    fun `should search logs`() {
        val logFile = File(tempDir, "test.jsonl")
        val writer = JsonlLogWriter(logFile)
        writer.append(LogEntry(Instant.now(), 1, "arduino", "MATCHING message"))
        writer.append(LogEntry(Instant.now(), 2, "arduino", "other message"))
        writer.close()

        val reader = JsonlLogReader(tempDir)
        val results = reader.searchLogs("MATCHING")
        assertEquals(1, results.size)
        assertEquals("MATCHING message", results[0].rawMessage)
    }
}
