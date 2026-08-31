AI Tool Interface Skill
Purpose

Use this skill when implementing the interface between the Arduino logging application and an AI harness.

Principle

Expose semantic debugging capabilities rather than infrastructure primitives.

Good:

log.status
log.search
log.read


Bad:

filesystem.read
shell.execute
serial.write

Initial tools

Implement or design toward:

log.start

Starts a serial logging session.

Inputs:

port
baudRate


Returns:

sessionId
status
port
baudRate
startedAt

log.stop

Stops the active logging session.

Returns:

sessionId
status
stoppedAt
linesReceived

log.status

Returns the current logger state.

log.list

Lists available log sessions.

Support bounded results.

log.read

Reads a bounded section of a log.

Support:

session ID
start position or time
maximum entries

Never return an unbounded log.

log.search

Searches captured logs.

Support:

session ID
text query
optional start time
optional end time
result limit
AI-oriented responses

Responses should be:

structured
bounded
deterministic
concise
easy for a model to interpret

Avoid returning huge blobs.

For example:

{
"sessionId": "abc123",
"matchCount": 3,
"truncated": false,
"entries": [
{
"timestamp": "...",
"sequence": 120,
"rawMessage": "ERROR MOTOR TIMEOUT"
}
]
}

Errors

Return machine-readable error codes plus human-readable messages.

Example:

{
"error": {
"code": "LOGGER_NOT_RUNNING",
"message": "No serial logging session is currently active."
}
}

Safety

The AI tool interface must not provide arbitrary:

shell commands
filesystem access
serial writes
process execution

unless explicitly added as a separately reviewed capability.

Future compatibility

Keep tool names and semantics stable.

The underlying storage or serial implementation should be replaceable without changing the AI-facing contract unnecessarily.

The AI-facing layer should eventually be adaptable to MCP or another supported harness protocol without requiring changes to the serial subsystem.