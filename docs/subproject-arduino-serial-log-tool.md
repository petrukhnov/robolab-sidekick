
# Arduino Serial Log Tool.

This project is a Kotlin/JVM application that connects to an Arduino over a serial port, captures serial output, persists it as structured logs, and exposes operations that can later be consumed by an AI harness.

The application is intended for robotics development and debugging.

# Primary goals
* Reliably read Arduino serial output.
* Timestamp every received log line.
* Preserve the original serial message without modification.
* Persist logs as append-only JSON Lines (.jsonl).
* Provide APIs for starting, stopping, inspecting, listing, reading, and searching logs.
* Keep serial communication, storage, and AI/tool integration separated.
* Make the serial layer testable without physical hardware.
* Make failures explicit and observable.
* Keep the first implementation small and deterministic.

# Non-goals for v1

Do not implement:
* robot control
* arbitrary command execution on the Arduino
* telemetry visualization
* cloud synchronization
* distributed logging
* database storage
* LLM reasoning
* automatic anomaly detection
* autonomous reconnection policies beyond what is explicitly required
* ROS integration
* hardware-specific Arduino parsing

These may be added later.

# Architecture

Use the following dependency direction:

Tool/API layer
│
▼
Application/service layer
│
├───────────────┐
▼               ▼
Serial abstraction   Log storage abstraction
│               │
▼               ▼
Real serial         JSONL filesystem
implementation


The domain/application layer must not depend directly on a specific serial-port library.

# Core abstractions

Prefer interfaces similar to:

interface SerialConnection {
fun connect()
fun disconnect()
fun isConnected(): Boolean
fun readLine(): String?
}

interface LogWriter {
fun append(entry: LogEntry)
fun flush()
fun close()
}


The exact API may evolve, but preserve the separation of concerns.

# Log entry

Every received serial line must have:

timestamp
monotonically increasing sequence number
source
original raw message

Example:

{
"timestamp": "2026-08-31T20:41:13.245Z",
"sequence": 18231,
"source": "arduino",
"rawMessage": "MOTOR LEFT CURRENT: 4.82A"
}


Do not parse or alter the Arduino message in the raw log.

# Timestamp rules

Use an explicit, timezone-aware timestamp representation.

Prefer UTC for persisted timestamps.

The timestamp represents when the JVM receives the serial line, not when the Arduino generated it, unless Arduino-generated timestamps are explicitly available and separately represented.

# Storage rules

Use append-only JSONL files for v1.

Each line must represent exactly one LogEntry.

Do not overwrite existing log entries.

Do not silently discard malformed records.

Use deterministic file naming.

Keep storage implementation behind an abstraction so that a database or remote store can be introduced later.

# Serial rules

The serial library is an implementation detail.

Do not expose third-party serial-library classes outside the serial adapter package.

The rest of the application should work against project-owned interfaces.

Handle:

missing port
permission errors
connection failure
disconnection
read errors
shutdown

explicitly.

Never silently swallow serial exceptions.

# Concurrency

The logger must support a single active logging session.

Starting an already-running logger must produce a clear error or idempotent result; choose one behavior and document it.

Stopping an inactive logger must be deterministic.

Do not introduce unnecessary concurrency.

Prefer a simple dedicated reader loop over complex reactive infrastructure for v1.

# AI/tool interface

The AI-facing interface should expose semantic operations rather than low-level filesystem or serial operations.

Preferred operations:

log.start
log.stop
log.status
log.list
log.read
log.search


Do not expose:

execute_sql
read_arbitrary_file
execute_shell
send_arbitrary_serial_command


The AI interface must not bypass application-level validation.

# Security

Treat the tool as potentially callable by an AI agent.

Never provide arbitrary shell execution.

Never allow arbitrary filesystem paths without validation.

Never expose unrestricted serial writes in v1.

Read-only log operations should be clearly separated from operations that change application state.

# Testing

Unit tests must not require an Arduino.

Use fake/in-memory implementations of:

serial connection
log writer
clock where useful

Test at least:

successful connection
serial line capture
timestamps
sequence numbers
JSONL persistence
empty lines
special characters
Unicode
serial disconnection
read failure
logger shutdown
repeated start
repeated stop
log search
log limits

Add integration tests for the filesystem implementation.

Hardware tests may exist separately but must not be required for normal CI.

# Dependencies

Keep dependencies minimal.

Before adding a dependency, determine whether the functionality can reasonably be implemented using the JDK/Kotlin standard library.

For serial communication, use a maintained JVM-compatible library, but isolate it behind the project's serial abstraction.

# Code style

Use idiomatic Kotlin.

Prefer:

immutable data
explicit types at public boundaries
small classes
constructor injection
sealed types for meaningful state/result hierarchies
java.time for timestamps
structured error handling

Avoid:

global mutable state
static service locators
unnecessary frameworks
reflection
excessive abstraction
giant service classes
hidden background threads

# Error handling

Errors returned to the tool/harness layer must be understandable to an engineer and useful to an AI model.

Prefer structured errors such as:

{
"code": "SERIAL_PORT_NOT_FOUND",
"message": "Serial port /dev/ttyACM0 was not found."
}


Do not expose stack traces as the primary user-facing error.

Stack traces should remain available for developer diagnostics.

# Observability

The application itself should emit useful diagnostic logs.

Distinguish between:

application diagnostics
captured Arduino logs

Never mix the application's own logs into the Arduino JSONL stream.

# Documentation

Keep README.md updated with:

prerequisites
supported platforms
configuration
how to identify a serial port
how to start logging
log format
development commands
testing commands

Document architectural decisions when they materially affect future tool integration.

# Implementation discipline

Work incrementally.

Before implementing a feature:

Inspect the existing project.
Identify affected interfaces.
Make the smallest coherent change.
Add or update tests.
Run formatting/static checks if configured.
Run tests.
Update documentation when behavior changes.

Do not perform broad refactors unrelated to the requested feature.

# Definition of done

A feature is not complete until:

implementation exists
tests exist where applicable
failure behavior is defined
public behavior is documented
existing tests pass
no unnecessary dependencies were introduced