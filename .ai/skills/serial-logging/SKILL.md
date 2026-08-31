# Serial Logging Skill

## Purpose

Use this skill when implementing Arduino serial communication and log capture.

## Responsibility

The serial subsystem is responsible only for:
- Locating/opening a serial connection
- Configuring baud rate and required serial settings
- Reading serial data
- Detecting connection/read failures
- Closing the connection

It must not own log-file formatting or AI tool behavior.

## Abstraction

Use a project-owned interface for serial communication.

**Example:**

```kotlin
interface SerialConnection {
    fun connect()
    fun disconnect()
    fun isConnected(): Boolean
    fun readLine(): String?
}
```

The exact interface may evolve, but third-party serial APIs must remain behind an adapter.

## Serial Configuration

At minimum support:
- Port
- Baud rate

Keep room for:
- Data bits
- Stop bits
- Parity
- Flow control

Do not expose unnecessary configuration until required.

## Input Handling

Preserve the original Arduino line. Do not:
- Trim meaningful content
- Change character case
- Parse messages in the serial layer
- Infer log severity
- Infer telemetry

If line termination is removed by the reader, document that behavior.

## Lifecycle

Connection state must be explicit.

**Expected lifecycle:**

```text
DISCONNECTED
│
▼
CONNECTING
│
▼
CONNECTED
│
├── read
│
▼
DISCONNECTED / FAILED
```

Shutdown must close the underlying serial resource.

## Failures

Handle explicitly:
- Port does not exist
- Permission denied
- Port already in use
- Connection failure
- Unexpected disconnection
- Read failure
- Malformed byte input

Do not silently retry forever. If automatic reconnect is introduced later, make it an explicit application feature with bounded retry behavior.

## Testing

Provide a fake serial implementation. Tests should be able to inject:
- `BOOT`
- `READY`
- `ERROR MOTOR TIMEOUT`

and verify the resulting `LogEntry` objects without hardware.

## Hardware Independence

The normal test suite must run without an Arduino connected. Hardware-dependent tests must be clearly separated from unit/integration tests.