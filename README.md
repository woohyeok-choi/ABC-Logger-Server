# ABC Logger Server

## Directory Structure
* [grpc](grpc): a sub-module for gRPC specification.
* [docker](docker): Dockerfile for a server.
* [src](src): Sources
  * [common](src/main/kotlin/kaist/iclab/abclogger/common): Common functions
  * [db](src/main/kotlin/kaist/iclab/abclogger/db): Database connection
  * [interceptor](src/main/kotlin/kaist/iclab/abclogger/interceptor): gRPC server interceptor
  * [schema](src/main/kotlin/kaist/iclab/abclogger/schema): Data schema
  * [service](src/main/kotlin/kaist/iclab/abclogger/service): gRPC service implementations

## Related Projects
* [ABC Logger Android Client](https://github.com/woohyeok-choi/ABC-Logger)
* [ABC Logger gRPC specification](https://github.com/woohyeok-choi/ABC-Logger-gRPC-Specs)
* [ABC Logger gRPC communication for Python](https://github.com/woohyeok-choi/ABC-Logger-CRUD-Boilerplate)
