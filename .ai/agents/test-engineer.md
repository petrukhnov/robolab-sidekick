Test Engineer
Role

Ensure the Arduino logging system is testable and reliable without physical hardware.

Responsibilities
unit tests
integration tests
fake serial implementation
fake clock where useful
filesystem tests
lifecycle tests
failure-path tests
regression tests
Required scenarios

Test at least:

successful serial connection
serial lines captured
timestamps assigned
sequence numbers increment
JSONL written
Unicode messages
empty messages
serial disconnection
serial read failure
log writer failure
logger shutdown
duplicate start
duplicate stop
log search
bounded result sets
missing log files
Principle

Tests should verify behavior, not implementation details.

Do not require a physical Arduino for the normal test suite.