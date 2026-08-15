# Repository Guidelines

## Project Structure & Module Organization

This repository is an sbt 2-only Scala plugin. Production code lives in
`src/main/scala/explicitdeps/`; `ExplicitDepsPlugin.scala` defines sbt keys and
tasks, while `Logic.scala` and supporting types contain dependency analysis.
Unit tests are in `src/test/scala/explicitdeps/`. End-to-end scripted fixtures
are under `src/sbt-test/basic/<scenario>/`, each with its own `build.sbt`,
sample sources, and a `test` script. Build configuration is in `build.sbt` and
`project/`. Keep licensing and upstream attribution in `LICENSE` and `NOTICE`
intact.

## Build, Test, and Development Commands

- `sbt compile` compiles the plugin against the configured sbt baseline.
- `sbt test` runs the MUnit unit-test suite.
- `sbt scripted` runs all scripted integration scenarios; use
  `sbt "scripted basic/scala3-success"` to focus on one fixture.
- `sbt clean test` is useful after changing compiler-analysis or build logic.

Use sbt 2.0.4 as declared in `project/build.properties`; do not introduce sbt
1 compatibility code.

## Coding Style & Naming Conventions

Write Scala 3 using two-space indentation and significant indentation (for
example, `class LogicSuite extends FunSuite:`). Use `PascalCase` for types and
objects, `camelCase` for methods, values, and sbt keys, and descriptive test
names in sentence form. Keep implementation in the `explicitdeps` package.
There is no checked-in formatter or linter, so follow the surrounding style and
avoid unrelated reformatting. Preserve copyright/SPDX headers in source files.

## Testing Guidelines

Add focused MUnit tests for pure logic, naming them `test("expected behavior")`.
Add or update a scripted fixture when a change affects plugin wiring, dependency
resolution, compiler analysis, or user-facing sbt output. Scripted tests should
exercise the plugin as a consumer would and assert expected success or failure.
Run `sbt test` before submitting; run `sbt scripted` for behavior changes.

## Commit & Pull Request Guidelines

Recent history uses Conventional Commit-style subjects, such as
`feat(core): complete the core sbt 2 port logic` and `chore(openspec): init`.
Use an imperative, scoped subject where useful. Keep commits narrowly focused.
Pull requests should explain the behavior change, note tests run, link relevant
issues or OpenSpec change material, and include output or screenshots only when
they clarify a user-visible reporting change.
