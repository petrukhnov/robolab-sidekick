# Kotlin/JVM Engineering Skill

## Purpose

Use this skill when implementing or reviewing Kotlin/JVM code in the Arduino log tool.

## Principles

Write simple, idiomatic Kotlin. Prefer:
- Immutable data
- Constructor injection
- Small focused classes
- Interfaces at architectural boundaries
- Explicit domain types
- `java.time.Instant` for timestamps
- `kotlin.io` and JDK APIs where sufficient
- Standard library functionality before adding dependencies

## Architecture

Keep these concerns separate:
- Domain
- Application
- Infrastructure
- Tool/API

Domain code must not depend on serial-port libraries or filesystem-specific implementation details.

## Nullability

- Do not use `!!` unless there is a strong, documented invariant.
- Prefer explicit nullable handling or domain-specific result types.

## Exceptions

Do not use exceptions as normal business-state communication. Exceptions are appropriate for infrastructure failures such as:
- Serial connection failure
- Filesystem failure
- Malformed external data

Convert infrastructure exceptions into meaningful application/tool errors at the appropriate boundary.

## Concurrency

Avoid concurrency unless it is necessary. For serial reading, a single dedicated reader mechanism is preferred for v1.

Any background execution must have:
- Deterministic shutdown
- Clear ownership
- No leaked threads
- Testable lifecycle behavior

## Public APIs

- Public interfaces should be small.
- Do not expose implementation-library types.
- For example, do not expose a third-party `SerialPort` object from the project's `SerialConnection`.

## Testing

- Prefer tests that verify observable behavior.
- Use fakes for external dependencies.
- Avoid tests that require:
  - A physical Arduino
  - A particular developer machine
  - A specific serial port
  - Wall-clock timing where a fake clock can be used

## Code Quality

Avoid premature abstraction. Do not introduce:
- Dependency injection frameworks
- Reactive frameworks
- Event buses
- Large application frameworks

unless the project requirements later justify them.