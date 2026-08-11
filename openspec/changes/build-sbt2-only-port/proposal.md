## Why

`sbt-explicit-dependencies` is useful for detecting accidental compile-time use of transitive dependencies and declarations that compilation does not need, but its published implementation supports only sbt 1.x. sbt 2 plugins run on Scala 3 and expose compiler-analysis file references differently, so users of sbt 2 need an independently maintained port.

## What Changes

- Create an independent, sbt 2-only implementation of the original Apache-2.0-licensed plugin.
- **BREAKING**: Support sbt 2 only; do not retain sbt 1.x or Scala 2.12 plugin compatibility.
- Provide the existing public diagnostics and failure tasks for undeclared and unused compile dependencies, including filtering semantics.
- Derive dependencies used by compilation from Zinc analysis and map them to module identities through sbt classpath metadata.
- Use POM/Ivy cache metadata only as a logged fallback when classpath metadata cannot identify an artifact.
- Preserve upstream licensing and clear credit to Chris Birchall and `cb372/sbt-explicit-dependencies`, while using independent project and publishing coordinates.

## Capabilities

### New Capabilities

- `sbt2-explicit-dependency-analysis`: Detect and report direct compile-time dependency mismatches in sbt 2 projects.
- `upstream-attribution`: Preserve Apache-2.0 licensing and identify the project as an independent port of the original plugin.

### Modified Capabilities

- None.

## Impact

- Adds the plugin source, sbt 2/Scala 3 build definition, tests, documentation, and CI/publishing configuration to this repository.
- Uses sbt's compiler analysis and classpath module metadata; compiler analysis remains an internal sbt API and will require compatibility testing against supported sbt 2 releases.
- Preserves the original task names and user-facing behavior, while the plugin artifact coordinates and repository identity are new.
