Log Storage Skill
Purpose

Use this skill when implementing persistence and querying of captured Arduino logs.

Storage format

Use JSON Lines (.jsonl) for v1.

One log entry per line.

Example:

{"timestamp":"2026-08-31T20:41:13.245Z","sequence":1,"source":"arduino","rawMessage":"BOOT"}

Raw data preservation

rawMessage is the source-of-truth representation of the received Arduino line.

Never rewrite or normalize it for storage.

If parsed fields are added later, they must be additional fields.

File lifecycle

A logging session should write to a deterministic session file.

Example:

logs/
robot-001/
2026-08-31T20-41-13Z.jsonl


Avoid overwriting an existing session.

Writes

Writes must be append-only.

Flush behavior should be explicit.

The implementation must balance durability and performance.

For v1, favor reliable persistence over maximum throughput.

Corruption

If an application crash leaves the final JSONL line incomplete, future implementations should be able to detect and handle it.

Do not silently rewrite historical entries.

Queries

Log search should support at least:

text query
time range
maximum result count

Potential future filters:

severity
component
source
sequence range

Do not build a database-backed query system for v1.

Security

Do not allow unrestricted filesystem paths from an AI-facing interface.

Resolve log files relative to the configured log root and validate paths.

Testing

Test:

append
multiple entries
Unicode
quotes
newlines where applicable
large messages
file creation
file reopening
search
result limits
missing files
malformed JSON lines