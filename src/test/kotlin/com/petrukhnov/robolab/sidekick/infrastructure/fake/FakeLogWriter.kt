package com.petrukhnov.robolab.sidekick.infrastructure.fake

import com.petrukhnov.robolab.sidekick.domain.LogEntry
import com.petrukhnov.robolab.sidekick.infrastructure.LogWriter

class FakeLogWriter : LogWriter {
    val entries = mutableListOf<LogEntry>()
    var closed = false
    var flushed = false

    override fun append(entry: LogEntry) {
        entries.add(entry)
    }

    override fun flush() {
        flushed = true
    }

    override fun close() {
        closed = true
    }
}
