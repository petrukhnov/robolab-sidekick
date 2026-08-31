package com.petrukhnov.robolab.sidekick.infrastructure.storage

import com.petrukhnov.robolab.sidekick.domain.LogEntry
import com.petrukhnov.robolab.sidekick.infrastructure.LogReader
import kotlinx.serialization.json.Json
import java.io.File

class JsonlLogReader(private val storageDir: File) : LogReader {
    private val json = Json

    override fun listLogs(): List<String> {
        return storageDir.listFiles { _, name -> name.endsWith(".jsonl") }
            ?.map { it.name }
            ?.sortedDescending()
            ?: emptyList()
    }

    override fun readLog(logName: String, limit: Int, offset: Long): List<LogEntry> {
        val file = File(storageDir, logName)
        if (!file.exists()) return emptyList()

        return file.useLines { lines ->
            lines.drop(offset.toInt())
                .take(limit)
                .map { json.decodeFromString<LogEntry>(it) }
                .toList()
        }
    }

    override fun searchLogs(query: String, limit: Int): List<LogEntry> {
        // Simple search across all logs
        return listLogs().flatMap { logName ->
            val file = File(storageDir, logName)
            file.useLines { lines ->
                lines.map { json.decodeFromString<LogEntry>(it) }
                    .filter { it.rawMessage.contains(query, ignoreCase = true) }
                    .toList()
            }
        }.take(limit)
    }
}
