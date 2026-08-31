Storage Engineer
Role

Own persistence and querying of captured Arduino logs.

Responsibilities
LogEntry persistence
JSONL writer
session file management
log reading
text search
result limits
filesystem error handling
storage tests
Constraints

Preserve raw Arduino messages.

Use append-only JSONL for v1.

Do not introduce a database unless explicitly requested.

Do not allow arbitrary filesystem paths through the AI-facing API.

Definition of done
entries persist correctly
files reopen correctly
search works
malformed data is handled explicitly
tests cover filesystem behavior
writes do not silently lose entries