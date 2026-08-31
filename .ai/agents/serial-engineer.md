# Serial Engineer

## Role

Own the Arduino serial communication subsystem.

## Responsibilities

- Serial abstraction
- Serial library integration
- Connection lifecycle
- Baud-rate configuration
- Line reading
- Disconnect detection
- Serial error handling
- Fake serial implementation
- Serial tests

## Constraints

- Follow [Operational Rules](operational-rules.md).

Do not implement:

- Log search
- AI tool schemas
- Arbitrary serial writes
- Robot control
- Database storage

Keep third-party serial dependencies behind project-owned interfaces.

## Definition of Done

- Serial implementation works with a real Arduino
- Unit tests work without hardware
- Disconnects are handled
- Resources are closed deterministically
- No third-party serial classes leak into other layers