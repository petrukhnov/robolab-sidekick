package com.petrukhnov.robolab.sidekick.api

import com.petrukhnov.robolab.sidekick.service.LoggingService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LogTool(private val loggingService: LoggingService) {
    private val json = Json { prettyPrint = true }

    fun start(port: String, baudRate: Int): String {
        return try {
            val result = loggingService.start(port, baudRate)
            json.encodeToString(result)
        } catch (e: Exception) {
            json.encodeToString(ErrorResponse(ErrorDetail("START_FAILED", e.message ?: "Unknown error")))
        }
    }

    fun stop(): String {
        return try {
            val result = loggingService.stop()
            json.encodeToString(result)
        } catch (e: Exception) {
            json.encodeToString(ErrorResponse(ErrorDetail("STOP_FAILED", e.message ?: "Unknown error")))
        }
    }

    fun status(): String {
        return try {
            val result = loggingService.getStatus()
            json.encodeToString(result)
        } catch (e: Exception) {
            json.encodeToString(ErrorResponse(ErrorDetail("STATUS_FAILED", e.message ?: "Unknown error")))
        }
    }

    fun list(): String {
        return try {
            val result = loggingService.listLogs()
            json.encodeToString(result)
        } catch (e: Exception) {
            json.encodeToString(ErrorResponse(ErrorDetail("LIST_FAILED", e.message ?: "Unknown error")))
        }
    }

    fun listPorts(): String {
        return try {
            val result = loggingService.listPorts()
            json.encodeToString(result)
        } catch (e: Exception) {
            json.encodeToString(ErrorResponse(ErrorDetail("LIST_PORTS_FAILED", e.message ?: "Unknown error")))
        }
    }

    fun read(sessionId: String, limit: Int = 100, offset: Long = 0): String {
        return try {
            val result = loggingService.readLog(sessionId, limit, offset)
            json.encodeToString(result)
        } catch (e: Exception) {
            json.encodeToString(ErrorResponse(ErrorDetail("READ_FAILED", e.message ?: "Unknown error")))
        }
    }

    fun search(query: String, limit: Int = 100): String {
        return try {
            val result = loggingService.searchLogs(query, limit)
            json.encodeToString(result)
        } catch (e: Exception) {
            json.encodeToString(ErrorResponse(ErrorDetail("SEARCH_FAILED", e.message ?: "Unknown error")))
        }
    }
}
