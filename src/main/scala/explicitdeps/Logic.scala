/*
 * Copyright 2018-2023 Chris Birchall
 * Copyright 2026 Michael Nedokushev
 * Licensed under the Apache License, Version 2.0.
 * Modified for this independent sbt 2-only port by Michael Nedokushev, 2026.
 */
package explicitdeps

import sbt.librarymanagement.{ Binary, DependencyFilter, Full, ModuleFilter, ModuleID }
import sbt.util.Logger

object Logic:
  def declared(libraryDeps: Seq[ModuleID], log: Logger): Set[Dependency] =
    val result = libraryDeps
      .filter(isCompileDependency)
      .filterNot(isScalaLibrary)
      .map { module =>
        Dependency(module.organization, module.name, module.revision, module.crossVersion.isInstanceOf[Binary | Full])
      }
      .toSet
    log.debug(s"Declared dependencies:\n${result.mkString("  ", "\n  ", "")}")
    result

  def undeclared(
    project: String,
    used: Set[Dependency],
    declared: Set[Dependency],
    filter: ModuleFilter,
    log: Logger
  ): Set[Dependency] =
    report(
      project,
      used diff declared,
      filter,
      "The project depends on the following libraries for compilation but they are not declared in libraryDependencies",
      "The project explicitly declares all the libraries that it directly depends on for compilation. Good job!",
      log
    )

  def unused(
    project: String,
    used: Set[Dependency],
    declared: Set[Dependency],
    filter: ModuleFilter,
    log: Logger
  ): Set[Dependency] =
    report(
      project,
      declared diff used,
      filter,
      "The following libraries are declared in libraryDependencies but are not needed for compilation",
      "The project has no unused dependencies declared in libraryDependencies. Good job!",
      log
    )

  private def report(
    project: String,
    candidates: Set[Dependency],
    filter: ModuleFilter,
    problem: String,
    success: String,
    log: Logger
  ): Set[Dependency] =
    val result = candidates.filter(dep => filter(ModuleID(dep.organization, dep.name, dep.version)))
    if result.nonEmpty then
      log.warn(
        s"$project >>> $problem:\n - ${result.toList.sortBy(dep => s"${dep.organization} ${dep.name}").mkString("\n - ")}"
      )
    else log.info(s"$project >>> $success")
    result

  private def isScalaLibrary(module: ModuleID): Boolean      =
    Set("scala-library", "scalajs-library", "scala3-library").contains(module.name)
  private def isCompileDependency(module: ModuleID): Boolean = module.configurations.forall(
    _.split("; ?").exists(c => c.startsWith("compile") || c.startsWith("provided") || c.startsWith("optional"))
  )
