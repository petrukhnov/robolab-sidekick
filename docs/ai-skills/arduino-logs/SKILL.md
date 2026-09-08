---
name: arduino-logs
description: Robolab Sidekick MCP operations and workflow for discovering serial ports, managing Arduino serial logging sessions, and coordinating interactive hardware tests.
---

# Arduino Serial Logs

Robolab Sidekick supports MCP, allowing AI agents (like Claude or IDE plugins) to interact with robot logs directly.

Use this skill when working with an Arduino board connected through a Linux serial port and when firmware behavior needs to be observed, diagnosed, or verified through serial logs.

## Purpose

This skill provides Junie with a reliable workflow for:

- discovering connected Arduino serial ports;
- starting and stopping Arduino logging sessions;
- monitoring logger status;
- reading logs from a specific session;
- searching previously captured logs;
- using logs to diagnose firmware behavior;
- preserving logs in the project's `logs/` directory.

The Arduino logging functionality is provided by an MCP server started with:

```bash
java -jar tools/robolab-sidekick-0.1.0-SNAPSHOT-all.jar --mcp
```

The MCP server exposes the LogTool operations described below.

## MCP Operations

The tool exposes the following operations through MCP or the `LogTool` class:
* `list_ports`: List all available serial ports.
* `log_start(port, baudRate)`: Starts a new logging session.
* `log_stop()`: Stops the active logging session and returns a summary.
* `log_status()`: Returns the current state of the logger.
* `log_list()`: Lists all available log files.
* `log_read(sessionId, limit, offset)`: Reads entries from a specific session.
* `log_search(query, limit)`: Searches across logs for a specific string.

## Log format
Logs are stored as JSON lines:
```json
{
  "timestamp": "2026-08-31T20:41:13.245Z",
  "sequence": 1,
  "source": "arduino",
  "rawMessage": "MOTOR LEFT CURRENT: 4.82A"
}
```

## Interactive Arduino Testing

When a task requires interaction with the physical Arduino, Junie should take
the lead in coordinating the interaction rather than requiring the user to
describe every physical action through chat.

### Principle

Junie should:

1. Start or reuse an Arduino logging session.
2. Tell the user exactly what physical action to perform.
3. Wait for the user to perform the action.
4. Monitor Arduino logs using the MCP logging tools.
5. Detect the corresponding Arduino event in the logs.
6. Confirm that the action was detected.
7. Analyze the resulting behavior.
8. Give the user the next physical action when required.

The user should not need to report actions such as:

- "I pressed the button."
- "I released the button."
- "I pressed it again."
- "I waited two seconds."

Instead, Junie should explicitly instruct the user when an action is required
and use the Arduino logs to determine when the Arduino observed that action.

### Interactive Test Protocol

When a physical interaction is required:

#### 1. Prepare logging

Check the current logger state with:

`log_status`

If necessary:

`list_ports`

Then start logging:

`log_start(port, baudRate)`

Record the session ID.

#### 2. Establish a baseline

Read a small amount of recent output using:

`log_read(sessionId, limit, offset)`

Determine the normal idle state before asking the user to interact with
the hardware.

#### 3. Instruct the user

Give one clear physical instruction.

For example:

> Press the LEFT button once now. I will monitor the Arduino logs and tell you
> when the press is detected.

Do not ask the user to describe the action afterward.

#### 4. Monitor for the event

After instructing the user, repeatedly inspect the active logging session.

Use `log_read` to obtain newly generated entries.

Look for the expected event based on the firmware's actual logging messages.

For example:

```text
BUTTON LEFT PRESSED
```
or:

```text
button=LEFT,state=PRESSED
```

Do not assume a particular message format if it is not present in the firmware.

#### 5. Confirm detection

Once the expected event is observed, tell the user that the action was
detected.

For example:

> Detected the LEFT button press at sequence 142.

Include the relevant timestamp and raw message when useful.

#### 6. Analyze the response

Inspect subsequent log entries to determine what the firmware did in response.

For example:

```text
BUTTON LEFT PRESSED
MOTOR LEFT ENABLED
MOTOR LEFT CURRENT: 1.82A
```

Correlate these events with the firmware source code.

#### 7. Continue the test

If another physical action is required, give the user the next instruction.

For example:

> The press was detected and the motor started correctly. Now release the
> LEFT button.

Then continue monitoring the logs.

### Event Detection

Junie should prefer explicit firmware events when available.

For example, if the firmware produces:

```text
BUTTON LEFT PRESSED
```

use that event to detect the interaction.

If explicit events are unavailable, Junie may infer an interaction from
related state changes, but must clearly distinguish an inference from a
directly observed event.

Do not claim that a physical button was pressed solely because unrelated
Arduino output changed.

### Avoid Excessive Polling

Do not continuously call `log_read` with large overlapping ranges.

Track the last processed sequence number or offset and read only newly
available entries where possible.

For example:

```text
last processed offset = 120

read:
offset = 120
limit = 50

process new entries

update offset
```

### Timeout

When waiting for a physical interaction, use a reasonable timeout.

If the expected event does not appear, tell the user that the event has not
been detected yet.

For example:

> I haven't detected the button press yet. Please press the LEFT button once.

If the event still does not appear after a reasonable period, investigate:

- whether logging is still active;
- whether the correct serial port is being monitored;
- whether the firmware is running;
- whether the expected log message actually exists;
- whether the button is producing the expected firmware event.

Do not repeatedly ask the user to press the button without first checking the
logger and available evidence.

### Never Assume the User's Action

The user may physically interact with the Arduino at any time.

However, Junie should only consider an action part of the current test when
there is corresponding evidence in the Arduino logs.

The log is the source of truth for whether the Arduino detected the action.

### Test Example

For a button test:

**Junie:**
- Start logging.
- Inspect idle state.

**Junie:**
> "Press the LEFT button once now."

*[monitor MCP logs]*

**Arduino:**
```text
BUTTON LEFT PRESSED
```

**Junie:**
> "Detected LEFT button press."

*[read subsequent events]*

**Arduino:**
```text
MOTOR LEFT ENABLED
MOTOR LEFT CURRENT: 1.82A
```

**Junie:**
> "The button press was detected. The firmware enabled the left motor 38 ms later. Now release the button."

*[monitor MCP logs]*

**Arduino:**
```text
BUTTON LEFT RELEASED
```

**Junie:**
> "Detected LEFT button release. The motor was disabled 12 ms later."

### Important

When an interactive hardware test is in progress, Junie should remain
focused on the test and monitor the Arduino logs rather than immediately
ending the task after giving the user an instruction.

The purpose of the interaction is to let Junie coordinate the physical test
and observe its consequences through MCP.
