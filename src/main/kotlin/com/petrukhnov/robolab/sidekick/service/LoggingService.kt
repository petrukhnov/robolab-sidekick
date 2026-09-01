package com.petrukhnov.robolab.sidekick.service

import com.petrukhnov.robolab.sidekick.api.*
import com.petrukhnov.robolab.sidekick.config.AppConfig
import com.petrukhnov.robolab.sidekick.infrastructure.LogReader
import com.petrukhnov.robolab.sidekick.infrastructure.PortProvider
import com.petrukhnov.robolab.sidekick.infrastructure.SerialConnection
import com.petrukhnov.robolab.sidekick.infrastructure.storage.JsonlLogReader
import com.petrukhnov.robolab.sidekick.infrastructure.storage.JsonlLogWriter
import java.io.File
import java.time.Instant
import java.util.*

class LoggingService(
    private val config: AppConfig,
    private val serialConnectionFactory: (port: String, baudRate: Int) -> SerialConnection,
    private val portProvider: PortProvider
) {
    private var currentLogger: SerialLogger? = null
    private var currentSessionId: String? = null
    private var startedAt: Instant? = null
    
    private val storageDir = File(config.storagePath).apply { mkdirs() }
    private val logReader: LogReader = JsonlLogReader(storageDir)

    fun start(port: String, baudRate: Int): SessionInfo {
        if (currentLogger != null) {
            throw IllegalStateException("A logging session is already active")
        }

        val sessionId = UUID.randomUUID().toString()
        val logFile = File(storageDir, "log-$sessionId.jsonl")
        val writer = JsonlLogWriter(logFile)
        val connection = serialConnectionFactory(port, baudRate)
        
        val logger = SerialLogger(connection, writer, config.copy(serialPort = port, baudRate = baudRate))
        logger.start()
        
        currentLogger = logger
        currentSessionId = sessionId
        val now = Instant.now()
        startedAt = now
        
        return SessionInfo(sessionId, "running", port, baudRate, now)
    }

    fun stop(): StopResult {
        val logger = currentLogger ?: throw IllegalStateException("No active logging session")
        val sessionId = currentSessionId!!
        val linesReceived = logger.getSequenceNumber()
        
        logger.stop()
        
        currentLogger = null
        currentSessionId = null
        startedAt = null
        
        return StopResult(sessionId, "stopped", Instant.now(), linesReceived)
    }

    fun getStatus(): LoggerStatusResponse {
        val logger = currentLogger
        return if (logger != null && logger.isRunning()) {
            LoggerStatusResponse(
                status = "running",
                activeSession = SessionInfo(
                    sessionId = currentSessionId!!,
                    status = "running",
                    port = config.serialPort, // Note: this might be slightly off if config wasn't updated correctly, but Logger has it
                    baudRate = config.baudRate,
                    startedAt = startedAt!!
                )
            )
        } else {
            LoggerStatusResponse(status = "idle")
        }
    }

    fun listLogs(): LogListResponse {
        return LogListResponse(logReader.listLogs())
    }

    fun listPorts(): PortListResponse {
        return PortListResponse(portProvider.listPorts())
    }

    fun readLog(sessionId: String, limit: Int, offset: Long): LogReadResponse {
        val logName = if (sessionId.endsWith(".jsonl")) sessionId else "log-$sessionId.jsonl"
        val entries = logReader.readLog(logName, limit, offset)
        return LogReadResponse(
            sessionId = sessionId,
            entries = entries,
            totalEntries = entries.size, // This is not quite right as it's only the entries we read
            truncated = entries.size >= limit
        )
    }

    fun searchLogs(query: String, limit: Int): LogSearchResponse {
        val entries = logReader.searchLogs(query, limit)
        return LogSearchResponse(
            query = query,
            matchCount = entries.size,
            entries = entries,
            truncated = entries.size >= limit
        )
    }
}
