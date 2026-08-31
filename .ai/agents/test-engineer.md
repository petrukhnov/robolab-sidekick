# Test Engineer

## Role

Ensure the Arduino logging system is testable and reliable without physical hardware.

## Responsibilities

- Unit tests
- Integration tests
- Fake serial implementation
- Fake clock where useful
- Filesystem tests
- Lifecycle tests
- Failure-path tests
- Regression tests

## Required Scenarios

Test at least:

- Successful serial connection
- Serial lines captured
- Timestamps assigned
- Sequence numbers increment
- JSONL written
- Unicode messages
- Empty messages
- Serial disconnection
- Serial read failure
- Log writer failure
- Logger shutdown
- Duplicate start
- Duplicate stop
- Log search
- Bounded result sets
- Missing log files

## Constraints

- Follow [Operational Rules](operational-rules.md).

## Principle

Tests should verify behavior, not implementation details.

Do not require a physical Arduino for the normal test suite.