package com.petrukhnov.robolab.sidekick.infrastructure

import com.petrukhnov.robolab.sidekick.domain.LogEntry

interface LogReader {
    fun listLogs(): List<String>
    fun readLog(logName: String, limit: Int = 100, offset: Long = 0): List<LogEntry>
    fun searchLogs(query: String, limit: Int = 100): List<LogEntry>
}
