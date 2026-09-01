# Robolab Sidekick

Robolab Sidekick is a collection of tools designed to work with an AI harness to assist in developing and debugging robots.

## Arduino Serial Log Tool

The primary tool currently implemented is the Arduino Serial Log Tool. It connects to an Arduino via a serial port, captures output, and persists it as structured JSONL logs.

### Features
* **Real Serial Communication**: Uses `jSerialComm` to communicate with physical hardware.
* **Structured Logging**: Timestamps every line and assigns monotonically increasing sequence numbers.
* **AI-Facing Interface**: Provides semantic operations for starting, stopping, listing, and searching logs.
* **JSONL Storage**: Logs are stored in append-only JSON Lines format for easy parsing and reliability.

### MCP Support (Model Context Protocol)
Robolab Sidekick supports MCP, allowing AI agents (like Claude or IDE plugins) to interact with robot logs directly.

To use the tool in MCP mode, run the fat JAR with the `--mcp` flag:
```bash
java -jar build/libs/robolab-sidekick-0.1.0-SNAPSHOT-all.jar --mcp
```

### AI Tool Interface
The tool exposes the following operations through MCP or the `LogTool` class:
* `list_ports`: List all available serial ports.
* `log_start(port, baudRate)`: Starts a new logging session.
* `log_stop()`: Stops the active logging session and returns a summary.
* `log_status()`: Returns the current state of the logger.
* `log_list()`: Lists all available log files.
* `log_read(sessionId, limit, offset)`: Reads entries from a specific session.
* `log_search(query, limit)`: Searches across logs for a specific string.

### Configuration
Configuration is managed via `AppConfig`. Key settings:
* `storagePath`: Directory where logs are stored (default: `logs`).
* `logSource`: Source name for logs (default: `arduino`).

### Prerequisites
* JDK 25.

### Development Commands
* **Build**: `./gradlew build`
* **Test**: `./gradlew test`
* **Manual Test**: `./gradlew run --args="<port> [baudRate]"` (Run without args to list ports)

### Log Format
Logs are stored as JSON lines:
```json
{
  "timestamp": "2026-08-31T20:41:13.245Z",
  "sequence": 1,
  "source": "arduino",
  "rawMessage": "MOTOR LEFT CURRENT: 4.82A"
}
```
