## Purpose

Ensure this independent sbt 2 port preserves the original project's Apache-2.0
terms and gives users clear, durable attribution to its upstream authors.

## ADDED Requirements

### Requirement: Preserve upstream license terms
The distributed project SHALL include the upstream Apache License 2.0 text and SHALL retain applicable upstream copyright, patent, trademark, and attribution notices in copied source and distributions.

#### Scenario: Source distribution is inspected
- **WHEN** a user obtains the project's source distribution
- **THEN** it includes the Apache-2.0 license text and applicable retained upstream notices

### Requirement: Identify independent port status
The project documentation SHALL identify the project as an independently maintained sbt 2-only port of `cb372/sbt-explicit-dependencies`, credit Chris Birchall as the original author, and state that the port is not affiliated with or endorsed by the original project or author.

#### Scenario: User reads project documentation
- **WHEN** a user views the project README or equivalent primary documentation
- **THEN** they can identify the upstream project, its original author, and the port's independent status

### Requirement: Mark material modifications
Copied upstream files that are materially modified for the sbt 2 port SHALL carry a prominent notice describing the modification and its maintainer or date.

#### Scenario: Modified copied source is inspected
- **WHEN** a user opens a source file copied and materially changed from upstream
- **THEN** the file contains a prominent modification notice
