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

## Install

This is an sbt 2-only plugin. Add it to `project/plugins.sbt`:

```scala
addSbtPlugin("me.mnedokushev" % "sbt2-explicit-dependencies" % "<version>")
```

## Use

Run either reporting task during development:

```text
undeclaredCompileDependencies
unusedCompileDependencies
```

Use `undeclaredCompileDependenciesTest` and
`unusedCompileDependenciesTest` in CI to fail a build when either report is
non-empty. Suppress accepted findings independently with
`undeclaredCompileDependenciesFilter` and `unusedCompileDependenciesFilter`:

```scala
unusedCompileDependenciesFilter -= moduleFilter("org.example", "known-runtime-api")
```

The plugin analyses the libraries observed by **Compile** compiler analysis.
Compile, Provided, and Optional declarations are considered; Test and Runtime
dependencies are not. It cannot infer dependencies used only through
reflection, service loading, or external configuration.

## Compatibility and maintenance

The supported baseline is sbt **2.0.4** (Scala 3.8.4). Analysis reads Zinc's
internal `sbt.internal.inc.Analysis` API because that is the source of truth
for source-to-library use. This is intentionally tested in CI, but it is not a
stable public sbt API: new sbt 2 releases must be validated before being
declared supported.
