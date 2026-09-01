# Robolab Sidekick

Robolab Sidekick is a collection of tools designed to work with an AI harness to assist in developing and debugging robots.

## Arduino Serial Log Tool

The primary tool currently implemented is the Arduino Serial Log Tool. It connects to an Arduino via a serial port, captures output, and persists it as structured JSONL logs.

### Features
* **Real Serial Communication**: Uses `jSerialComm` to communicate with physical hardware.
* **Structured Logging**: Timestamps every line and assigns monotonically increasing sequence numbers.
* **AI-Facing Interface**: Provides semantic operations for starting, stopping, listing, and searching logs.
* **JSONL Storage**: Logs are stored in append-only JSON Lines format for easy parsing and reliability.

### AI Tool Interface
The tool exposes the following operations through the `LogTool` class:
* `log.start(port, baudRate)`: Starts a new logging session.
* `log.stop()`: Stops the active logging session and returns a summary.
* `log.status()`: Returns the current state of the logger.
* `log.list()`: Lists all available log files.
* `log.read(sessionId, limit, offset)`: Reads entries from a specific session.
* `log.search(query, limit)`: Searches across logs for a specific string.

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
