Serial Logging Skill
Purpose

Use this skill when implementing Arduino serial communication and log capture.

Responsibility

The serial subsystem is responsible only for:

locating/opening a serial connection
configuring baud rate and required serial settings
reading serial data
detecting connection/read failures
closing the connection

It must not own log-file formatting or AI tool behavior.

Abstraction

Use a project-owned interface for serial communication.

Example:

interface SerialConnection {
fun connect()
fun disconnect()
fun isConnected(): Boolean
fun readLine(): String?
}


The exact interface may evolve, but third-party serial APIs must remain behind an adapter.

Serial configuration

At minimum support:

port
baud rate

Keep room for:

data bits
stop bits
parity
flow control

Do not expose unnecessary configuration until required.

Input handling

Preserve the original Arduino line.

Do not:

trim meaningful content
change character case
parse messages in the serial layer
infer log severity
infer telemetry

If line termination is removed by the reader, document that behavior.

Lifecycle

Connection state must be explicit.

Expected lifecycle:

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


Shutdown must close the underlying serial resource.

Failures

Handle explicitly:

port does not exist
permission denied
port already in use
connection failure
unexpected disconnection
read failure
malformed byte input

Do not silently retry forever.

If automatic reconnect is introduced later, make it an explicit application feature with bounded retry behavior.

Testing

Provide a fake serial implementation.

Tests should be able to inject:

BOOT
READY
ERROR MOTOR TIMEOUT


and verify the resulting LogEntry objects without hardware.

Hardware independence

The normal test suite must run without an Arduino connected.

Hardware-dependent tests must be clearly separated from unit/integration tests.