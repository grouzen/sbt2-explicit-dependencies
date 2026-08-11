# sbt2-explicit-dependencies

An independently maintained, sbt 2-only port of
[cb372/sbt-explicit-dependencies](https://github.com/cb372/sbt-explicit-dependencies),
originally created by Chris Birchall. This project is not affiliated with or
endorsed by the original project or author.

The plugin detects two kinds of compile-time dependency drift:

- `undeclaredCompileDependencies` reports transitive libraries used directly
  by Compile sources but not declared in `libraryDependencies`.
- `unusedCompileDependencies` reports Compile-relevant direct dependencies not
  used by compilation.

It intentionally analyses compile-time use only; runtime-only dependencies
such as JDBC drivers should be declared in the `Runtime` configuration.

## License and attribution

This port is licensed under the Apache License, Version 2.0. It incorporates
and modifies work from `cb372/sbt-explicit-dependencies`, copyright Chris
Birchall. See [LICENSE](LICENSE) and [NOTICE](NOTICE) for the applicable terms
and attribution.

Maintained by Michael Nedokushev <michael.nedokushev@gmail.com>.
