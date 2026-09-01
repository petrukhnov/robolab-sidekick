package com.petrukhnov.robolab.sidekick.api

import com.petrukhnov.robolab.sidekick.domain.InstantSerializer
import com.petrukhnov.robolab.sidekick.domain.LogEntry
import com.petrukhnov.robolab.sidekick.infrastructure.PortInfo
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class PortListResponse(
    val ports: List<PortInfo>
)

@Serializable
data class SessionInfo(
    val sessionId: String,
    val status: String,
    val port: String,
    val baudRate: Int,
    @Serializable(with = InstantSerializer::class)
    val startedAt: Instant
)

@Serializable
data class StopResult(
    val sessionId: String,
    val status: String,
    @Serializable(with = InstantSerializer::class)
    val stoppedAt: Instant,
    val linesReceived: Long
)

@Serializable
data class LoggerStatusResponse(
    val status: String,
    val activeSession: SessionInfo? = null
)

@Serializable
data class LogListResponse(
    val logs: List<String>
)

@Serializable
data class LogReadResponse(
    val sessionId: String,
    val entries: List<LogEntry>,
    val totalEntries: Int,
    val truncated: Boolean
)

@Serializable
data class LogSearchResponse(
    val query: String,
    val matchCount: Int,
    val entries: List<LogEntry>,
    val truncated: Boolean
)

@Serializable
data class ErrorResponse(
    val error: ErrorDetail
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String
)
