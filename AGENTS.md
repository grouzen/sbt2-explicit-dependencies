# Repository Guidelines

## Project Structure & Module Organization

This repository is an sbt 1 and sbt 2 cross-built Scala plugin. Shared
production code lives in `src/main/scala/explicitdeps/`. Version-specific sbt
integration lives in `src/main/scala-2.12/explicitdeps/` for sbt 1 and
`src/main/scala-3/explicitdeps/` for sbt 2. Unit tests are in
`src/test/scala/explicitdeps/`. End-to-end scripted fixtures are under
`src/sbt-test/basic/<scenario>/`, each with its own `build.sbt`, sample sources,
and a `test` script. Build configuration is in `build.sbt` and `project/`. Keep
licensing and upstream attribution in `LICENSE` and `NOTICE` intact.

## Build, Test, and Development Commands

- `sbt '+compile'` compiles both plugin variants.
- `sbt '+test'` runs the MUnit unit-test suite for both variants.
- `sbt '++2.12.21' scripted` runs all scenarios on the sbt 1 baseline.
- `sbt '++3.8.4' scripted` runs all scenarios on the sbt 2 baseline.
- Add `"scripted basic/scala3-success"` to focus on one fixture.

The plugin compile baselines are sbt 1.3.13 / Scala 2.12.21 and sbt 2.0.4 /
Scala 3.8.4. CI also checks sbt 1.4.0 and 1.8.3 on Temurin 17, and sbt 1.12.14
and sbt 2.0.4 on Temurin 25. Keep shared behavior aligned across both
integrations.

## Coding Style & Naming Conventions

Write shared Scala in syntax accepted by both Scala 2.12 and Scala 3, using
two-space indentation and braces. Version-specific source may use the native
syntax of its Scala version. Use `PascalCase` for types and objects, `camelCase`
for methods, values, and sbt keys, and descriptive test names in sentence form.
Keep implementation in the `explicitdeps` package. Run `+scalafmtCheckAll` and
`+scalafix --check`, avoid unrelated reformatting, and preserve
copyright/SPDX headers in source files.

## Testing Guidelines

Add focused MUnit tests for pure logic, naming them `test("expected behavior")`.
Add or update a scripted fixture when a change affects plugin wiring, dependency
resolution, compiler analysis, or user-facing sbt output. Scripted tests should
exercise the plugin as a consumer would and assert expected success or failure.
Run `sbt '+test'` before submitting. Run scripted tests for both plugin variants
when behavior or integration changes, and use the CI runtime matrix for sbt 1
compatibility changes.

## Commit & Pull Request Guidelines

Recent history uses Conventional Commit-style subjects, such as
`feat(core): complete the core sbt 2 port logic` and `chore(openspec): init`.
Use an imperative, scoped subject where useful. Keep commits narrowly focused.
Pull requests should explain the behavior change, note tests run, link relevant
issues or OpenSpec change material, and include output or screenshots only when
they clarify a user-visible reporting change.
