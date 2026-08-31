# Storage Engineer

## Role

Own persistence and querying of captured Arduino logs.

## Responsibilities

- `LogEntry` persistence
- JSONL writer
- Session file management
- Log reading
- Text search
- Result limits
- Filesystem error handling
- Storage tests

## Constraints

- Follow [Operational Rules](operational-rules.md).

- Preserve raw Arduino messages.
- Use append-only JSONL for v1.
- Do not introduce a database unless explicitly requested.
- Do not allow arbitrary filesystem paths through the AI-facing API.

## Definition of Done

- Entries persist correctly
- Files reopen correctly
- Search works
- Malformed data is handled explicitly
- Tests cover filesystem behavior
- Writes do not silently lose entries