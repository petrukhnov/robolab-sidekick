package com.petrukhnov.robolab.sidekick.api.mcp

import com.petrukhnov.robolab.sidekick.service.LoggingService
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.types.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.io.asSource
import kotlinx.io.asSink
import kotlinx.io.buffered

class McpServer(private val loggingService: LoggingService) {
    private val json = Json { prettyPrint = true }

    private val server = Server(
        serverInfo = Implementation("robolab-sidekick", "0.1.0"),
        options = ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = false)
            )
        )
    ) {
        addTool(
            "list_ports",
            "List all available serial ports"
        ) {
            val result = loggingService.listPorts()
            CallToolResult(content = listOf(TextContent(json.encodeToString(result))))
        }

        addTool(
            "log_start",
            "Start a new logging session on a specified serial port"
        ) { request ->
            val args = request.arguments ?: throw IllegalArgumentException("Missing arguments")
            val port = args["port"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing port argument")
            val baudRate = args["baudRate"]?.jsonPrimitive?.intOrNull ?: 115200
            try {
                val result = loggingService.start(port, baudRate)
                CallToolResult(content = listOf(TextContent(json.encodeToString(result))))
            } catch (e: Exception) {
                CallToolResult(
                    content = listOf(TextContent("Error: ${e.message}")),
                    isError = true
                )
            }
        }

        addTool(
            "log_stop",
            "Stop the currently active logging session"
        ) {
            try {
                val result = loggingService.stop()
                CallToolResult(content = listOf(TextContent(json.encodeToString(result))))
            } catch (e: Exception) {
                CallToolResult(
                    content = listOf(TextContent("Error: ${e.message}")),
                    isError = true
                )
            }
        }

        addTool(
            "log_status",
            "Get the status of the serial logger"
        ) {
            val result = loggingService.getStatus()
            CallToolResult(content = listOf(TextContent(json.encodeToString(result))))
        }

        addTool(
            "log_list",
            "List available log files"
        ) {
            val result = loggingService.listLogs()
            CallToolResult(content = listOf(TextContent(json.encodeToString(result))))
        }

        addTool(
            "log_read",
            "Read entries from a specific log session"
        ) { request ->
            val args = request.arguments ?: throw IllegalArgumentException("Missing arguments")
            val sessionId = args["sessionId"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing sessionId argument")
            val limit = args["limit"]?.jsonPrimitive?.intOrNull ?: 100
            val offset = args["offset"]?.jsonPrimitive?.longOrNull ?: 0L
            try {
                val result = loggingService.readLog(sessionId, limit, offset)
                CallToolResult(content = listOf(TextContent(json.encodeToString(result))))
            } catch (e: Exception) {
                CallToolResult(
                    content = listOf(TextContent("Error: ${e.message}")),
                    isError = true
                )
            }
        }

        addTool(
            "log_search",
            "Search for specific text in all logs"
        ) { request ->
            val args = request.arguments ?: throw IllegalArgumentException("Missing arguments")
            val query = args["query"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing query argument")
            val limit = args["limit"]?.jsonPrimitive?.intOrNull ?: 100
            try {
                val result = loggingService.searchLogs(query, limit)
                CallToolResult(content = listOf(TextContent(json.encodeToString(result))))
            } catch (e: Exception) {
                CallToolResult(
                    content = listOf(TextContent("Error: ${e.message}")),
                    isError = true
                )
            }
        }
    }

    fun start() = runBlocking {
        val transport = StdioServerTransport(System.`in`.asSource().buffered(), System.out.asSink().buffered()) {}
        server.createSession(transport)
        kotlinx.coroutines.awaitCancellation()
        @Suppress("UNREACHABLE_CODE")
        Unit
    }
}
