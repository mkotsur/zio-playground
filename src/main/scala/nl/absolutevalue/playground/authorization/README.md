# Vision

An approach that allows to separate authentication from business logic in developer-friendly and type safe manner.

### IT SHOULD:
  ✅ separate authentication and authorisation
  ✅ separate permission validation from business logic
  ✅ allow modeling permissions as ADTs
  ✅ allow granular permissions configuration
  ✅ make impossible to add a route without proper authoriseDbReadWrite check
  - work with ZIO-http
  - work with Tapir

### IT SHOULD NOT:
  - be a replacement for standard JWT libs
  - require boilerplate code
  - trigger scalafix or compiler warnings
  - require deep knowledge of ZIO or FP
  - require a lot of rewrite to work with Scala 3
