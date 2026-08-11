## Context

The upstream plugin obtains the external libraries used by compilation from
Zinc's `Analysis.relations.allLibraryDeps`, but its sbt 1 implementation
converts virtual file entries through reflection and reconstructs module
identity by reading POM or Ivy files adjacent to a cached JAR. sbt 2 runs
plugins on Scala 3, provides `VirtualFileRef` entries, and supplies a
`FileConverter` to resolve them. See proposal.md and the dependency-analysis
spec for the required behavior.

## Goals / Non-Goals

**Goals:**

- Preserve the four established diagnostic task names and their observable
  results for sbt 2 builds.
- Identify used modules primarily from sbt-provided classpath metadata instead
  of cache-path conventions.
- Make the port Scala 3 and sbt 2 only, with focused scripted coverage for
  compatibility-sensitive behavior.
- Preserve Apache-2.0 obligations and upstream attribution in the repository
  and released documentation.

**Non-Goals:**

- sbt 1.x compatibility or cross-building one plugin binary for sbt 1 and 2.
- Detecting runtime-only use through reflection, service loading, or external
  configuration.
- Replacing or stabilising Zinc's internal analysis API.
- Changing the task names or expanding analysis to Test sources in this change.

## Decisions

### Keep Zinc analysis as the source of truth for actual source use

The diagnostic starts from `(Compile / compile).value` cast to the internal
Zinc `Analysis`, then reads `relations.allLibraryDeps`. This records libraries
used by compiler analysis, rather than every artifact resolved by dependency
management.

`fileConverter.value.toPath(ref)` resolves every `VirtualFileRef` to a path.
This replaces the upstream reflection, runtime-class-name check, manual
`${CSR_CACHE}`/`${BASE}` expansion, and private cache-directory lookup.

Alternatives considered:

- Compare against the resolved update report: rejected because it cannot
  distinguish resolved-but-unused transitive modules.
- Inspect source imports: rejected because it fails for Java use, fully
  qualified references, macro/compiler-generated use, and classpath-level
  dependencies.

### Map used paths to classpath module metadata first

Build a normalized index of the Compile external dependency classpath:

```text
Compile external classpath entries + attached module metadata
                         │
                         ▼
              normalized artifact path → ModuleID

Zinc allLibraryDeps → FileConverter paths
                         │
                         ▼
                     path lookup
                         │
                         ▼
                    Dependency identity
```

The exact sbt 2 API used to read classpath entry metadata will be verified
against the selected sbt baseline during implementation. The mapping must
preserve organization, name, resolved revision, and binary/full cross-version
semantics. Path normalization must use the same filesystem representation on
both sides.

If a used JAR has no classpath module metadata or no matching entry, retain the
upstream POM/Ivy parser as a secondary, warning-logged recovery mechanism.
Unknown artifacts do not fail analysis by themselves.

Alternatives considered:

- Retain POM/Ivy parsing as the primary mechanism: rejected because resolver
  cache layouts and metadata presence are not a reliable integration contract.
- Drop artifacts without metadata: rejected because it silently loses
  diagnostics that a fallback can recover.

### Preserve the comparison model and configuration semantics

Represent used and declared modules using one value type that records
organization, base name, resolved revision, and cross-version status. The
existing set-difference model remains:

```text
undeclared = used-by-Compile − declared-for-Compile
unused     = declared-for-Compile − used-by-Compile
```

Exclude Scala standard library artifacts from both inputs. Treat compile,
provided, and optional declarations as Compile-relevant; exclude Test and
Runtime declarations. Apply the existing user-facing module filters after set
comparison and before messages or failing checks.

### Define diagnostic tasks as uncached

sbt 2 caches tasks by default. Define both result-producing diagnostics and
the two failure tasks using `Def.uncached`, avoiding the need to introduce
serialization formats for analysis-derived result values and ensuring warnings
and failures are evaluated on each invocation.

### Port source and test structure rather than preserving binary compatibility

Copy the upstream logical components and scripted tests as a starting point,
then migrate sources to Scala 3 and slash-scoped sbt syntax. Retain the public
plugin auto-import surface. Add an explicit Scala XML dependency only if the
POM/Ivy fallback stays XML-based; replacing the fallback parser is permitted
provided its recovery behavior remains.

## Risks / Trade-offs

- [Zinc analysis is an internal sbt API] → Pin and document a supported sbt 2
  baseline, run scripted tests in CI, and test new sbt releases promptly.
- [Classpath metadata attachment API differs from expected shape] → Validate it
  in an early compile/test spike; use the retained POM/Ivy parser while keeping
  metadata mapping the primary path.
- [Path mismatch prevents module lookup] → Normalize resolved paths and add a
  test using Coursier-backed virtual file conversion.
- [A direct declaration has multiple artifacts/classifiers] → Match used
  artifacts independently to their module identity and cover classifier cases.
- [Analysis cannot observe runtime behavior] → Keep Runtime dependencies out of
  unused checks and document the compile-time boundary.

## Migration Plan

1. Establish the independent repository identity, Apache-2.0 license, and
   upstream attribution before copying source.
2. Introduce the sbt 2/Scala 3 plugin build and port source/test code.
3. Verify metadata-first mapping against the selected sbt release, with the
   fallback parser available only for unmatched artifacts.
4. Run scripted tests and publish only under new coordinates after the suite
   passes.

Rollback is release-level: do not publish a replacement under upstream
coordinates. If a release proves incompatible with an sbt 2 update, document
the supported range and ship a port-only compatibility fix.

## Open Questions

- Select final project name, Maven coordinates, and publishing/release plugin
  before publishing; these choices do not affect the port's behavior or core
  task breakdown.
