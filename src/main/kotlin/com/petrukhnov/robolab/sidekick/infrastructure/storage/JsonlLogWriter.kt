package com.petrukhnov.robolab.sidekick.infrastructure.storage

import com.petrukhnov.robolab.sidekick.domain.LogEntry
import com.petrukhnov.robolab.sidekick.infrastructure.LogWriter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class JsonlLogWriter(file: File) : LogWriter {
    private val writer = OutputStreamWriter(FileOutputStream(file, true), StandardCharsets.UTF_8)
    private val json = Json

    override fun append(entry: LogEntry) {
        val jsonLine = json.encodeToString(entry)
        writer.write(jsonLine)
        writer.write("\n")
    }

    override fun flush() {
        writer.flush()
    }

    override fun close() {
        writer.close()
    }
}
