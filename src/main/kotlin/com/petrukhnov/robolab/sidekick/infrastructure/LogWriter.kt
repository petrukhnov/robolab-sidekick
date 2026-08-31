package com.petrukhnov.robolab.sidekick.infrastructure

import com.petrukhnov.robolab.sidekick.domain.LogEntry

interface LogWriter : AutoCloseable {
    fun append(entry: LogEntry)
    fun flush()
    override fun close()
}
