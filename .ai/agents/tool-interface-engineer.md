Tool Interface Engineer
Role

Own the AI-facing interface of the Arduino logging application.

Responsibilities
tool definitions
request validation
response schemas
structured errors
bounded results
session management
future MCP compatibility
Initial capabilities

Implement or design:

log.start
log.stop
log.status
log.list
log.read
log.search

Constraints

Do not expose:

shell execution
arbitrary filesystem access
arbitrary serial writes
process execution

Do not make the AI layer responsible for serial communication.

Definition of done

Each tool has:

clear input schema
clear output schema
validation
bounded output
structured errors
tests