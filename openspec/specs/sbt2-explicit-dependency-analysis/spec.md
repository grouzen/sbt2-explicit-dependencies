# sbt2-explicit-dependency-analysis Specification

## Purpose

Provide sbt 2 projects with actionable diagnostics when declared compile
dependencies do not match the external libraries their sources use to compile.

## Requirements

### Requirement: Detect undeclared compile dependencies
The plugin SHALL provide `undeclaredCompileDependencies`, which identifies external library modules used by the project's Compile sources but not declared as direct Compile-relevant `libraryDependencies`. It SHALL report the identified modules and return them to the build.

#### Scenario: A transitive module is used directly
- **WHEN** Compile source code references a class from a resolved transitive dependency that is not directly declared
- **THEN** `undeclaredCompileDependencies` reports that module as undeclared

#### Scenario: All used modules are declared
- **WHEN** every external module used by Compile source code is directly declared as Compile-relevant
- **THEN** `undeclaredCompileDependencies` returns no modules and reports successful explicit declaration

### Requirement: Detect unused compile dependencies
The plugin SHALL provide `unusedCompileDependencies`, which identifies direct Compile-relevant `libraryDependencies` that are not used by the project's Compile sources. It SHALL report the identified modules and return them to the build.

#### Scenario: A declared module is not used
- **WHEN** a direct Compile-relevant dependency contributes no library used by Compile source code
- **THEN** `unusedCompileDependencies` reports that module as unused

#### Scenario: Every declared module is used
- **WHEN** every direct Compile-relevant dependency is used by Compile source code
- **THEN** `unusedCompileDependencies` returns no modules and reports successful dependency use

### Requirement: Provide build-failing checks
The plugin SHALL provide `undeclaredCompileDependenciesTest` and `unusedCompileDependenciesTest`. Each task SHALL fail the sbt build when its corresponding diagnostic finds one or more modules and SHALL succeed otherwise.

#### Scenario: Undeclared dependency check fails
- **WHEN** `undeclaredCompileDependenciesTest` finds an undeclared module
- **THEN** the task fails the build with an explanatory error

#### Scenario: Unused dependency check fails
- **WHEN** `unusedCompileDependenciesTest` finds an unused module
- **THEN** the task fails the build with an explanatory error

### Requirement: Respect dependency scope and filters
The analysis SHALL consider dependencies relevant to compilation, including compile, provided, and optional declarations, and SHALL exclude test- and runtime-only declarations. Each diagnostic SHALL apply its corresponding configurable module filter before reporting or failing for a module.

#### Scenario: Runtime-only dependency is excluded
- **WHEN** a dependency is declared only in the Runtime configuration
- **THEN** it is not reported as unused by `unusedCompileDependencies`

#### Scenario: Filter suppresses a diagnostic
- **WHEN** a configured module filter excludes a module otherwise found by a diagnostic
- **THEN** that module is omitted from the task result, output, and failure decision

### Requirement: Support Scala cross-versioned modules
The analysis SHALL correctly compare Scala binary- and full-cross-versioned modules to their direct declarations, while retaining the actual resolved module version in diagnostics.

#### Scenario: A Scala binary-cross-versioned module is used
- **WHEN** Compile source code uses an artifact whose resolved name has the project's Scala binary suffix
- **THEN** the diagnostic identifies it as the corresponding Scala-cross-versioned module

### Requirement: Diagnose from the compilation result
The diagnostics SHALL derive used external libraries from the project's sbt 2 Compile analysis rather than from the full resolved dependency graph. If a used artifact cannot be associated with a module identity, the plugin SHALL emit a diagnostic log message and continue analysing other artifacts.

#### Scenario: Resolved but unused transitive dependency
- **WHEN** a transitive dependency is resolved but no Compile source uses it
- **THEN** it is not reported as an undeclared dependency

#### Scenario: Artifact identity cannot be recovered
- **WHEN** a used external artifact has no recoverable module identity
- **THEN** the plugin logs that the artifact could not be classified and completes the task for the remaining artifacts
