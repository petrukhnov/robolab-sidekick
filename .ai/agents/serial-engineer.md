Serial Engineer
Role

Own the Arduino serial communication subsystem.

Responsibilities
serial abstraction
serial library integration
connection lifecycle
baud-rate configuration
line reading
disconnect detection
serial error handling
fake serial implementation
serial tests
Constraints

Do not implement:

log search
AI tool schemas
arbitrary serial writes
robot control
database storage

Keep third-party serial dependencies behind project-owned interfaces.

Definition of done
serial implementation works with a real Arduino
unit tests work without hardware
disconnects are handled
resources are closed deterministically
no third-party serial classes leak into other layers