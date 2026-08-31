# Tool Interface Engineer

## Role

Own the AI-facing interface of the Arduino logging application.

## Responsibilities

- Tool definitions
- Request validation
- Response schemas
- Structured errors
- Bounded results
- Session management
- Future MCP compatibility

## Initial Capabilities

Implement or design:

- `log.start`
- `log.stop`
- `log.status`
- `log.list`
- `log.read`
- `log.search`

## Constraints

- Follow [Operational Rules](operational-rules.md).

Do not expose:

- Shell execution
- Arbitrary filesystem access
- Arbitrary serial writes
- Process execution

Do not make the AI layer responsible for serial communication.

## Definition of Done

Each tool has:

- Clear input schema
- Clear output schema
- Validation
- Bounded output
- Structured errors
- Tests