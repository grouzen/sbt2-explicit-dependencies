## 1. Repository and sbt 2 build foundation

- [x] 1.1 Establish the independent project identity, retain the upstream Apache-2.0 license, and add durable README/source attribution and modification notices.
- [x] 1.2 Create the sbt 2-only plugin build using Scala 3, supported publishing/release tooling, and new plugin coordinates.
- [x] 1.3 Port the upstream source layout and public auto-import task/settings surface to Scala 3 and slash-scoped sbt syntax.
- [x] 1.4 Make the result and failure diagnostic tasks uncached for sbt 2 task evaluation.

## 2. Used-artifact to module mapping

- [x] 2.1 Verify the selected sbt 2 baseline's API for Compile external-classpath entries and their attached module metadata with a focused compile/test spike.
- [x] 2.2 Replace the upstream reflective virtual-file and Coursier-cache conversion with `fileConverter`-based conversion of Zinc `allLibraryDeps` entries.
- [x] 2.3 Build a normalized Compile classpath artifact-path-to-module index that retains organization, module name, resolved revision, and cross-version information.
- [x] 2.4 Map Zinc-used artifact paths through the classpath metadata index before constructing dependency identities for comparison.
- [x] 2.5 Retain or port POM/Ivy metadata recovery only as a warning-logged fallback for unmatched artifacts, including its Scala XML dependency or an equivalent parser.

## 3. Diagnostic behavior

- [x] 3.1 Port and adapt the declared-versus-used set-difference logic for undeclared and unused compile dependencies.
- [x] 3.2 Preserve Compile, Provided, and Optional inclusion; Test and Runtime exclusion; Scala standard library exclusion; and independent module filters.
- [x] 3.3 Preserve failure behavior and actionable messages for both `*Test` tasks.
- [x] 3.4 Add focused unit coverage for cross-versioned module identities and unknown-artifact fallback behavior.

## 4. sbt 2 integration verification

- [x] 4.1 Port existing scripted scenarios for undeclared, unused, filter, Java, compiler-plugin, classifier, multi-module, and configuration behavior to sbt 2. (Compiler-plugin scenario intentionally omitted: kind-projector is unsupported on Scala 3.)
- [x] 4.2 Add scripted coverage proving a used transitive artifact is identified through metadata-first classpath mapping.
- [x] 4.3 Add scripted coverage for Coursier/virtual-file path conversion and Scala 3 project sources.
- [x] 4.4 Run the full scripted suite against the selected sbt 2 baseline and fix behavioral regressions.
- [x] 4.5 Port the remaining applicable upstream scripted scenarios: full Scala-version cross-versioning, Scala-library exclusion, provided/optional declarations, and Coursier resolution; replace sbt 1-only cases with equivalent Scala 3 coverage.
- [x] 4.6 Run the expanded scripted suite against sbt 2.0.4 and address regressions.

## 5. Documentation and delivery

- [x] 5.1 Update installation and usage documentation for new sbt 2-only coordinates, retained task names, filters, and compile-time limitations.
- [x] 5.2 Document the supported sbt 2 baseline and the maintenance implication of relying on Zinc's internal analysis API.
- [x] 5.3 Update CI to compile and run scripted tests on the supported sbt 2 version and validate publishing metadata.
