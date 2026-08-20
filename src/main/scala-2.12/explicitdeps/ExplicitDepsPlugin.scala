/*
 * Copyright 2018-2023 Chris Birchall
 * Copyright 2026 Michael Nedokushev
 * Licensed under the Apache License, Version 2.0.
 * Modified for this independent sbt port by Michael Nedokushev, 2026.
 */
package explicitdeps

import sbt.Keys._
import sbt.librarymanagement.{ DependencyFilter, ModuleFilter }
import sbt.{ ScalaVersion => _, _ }

object ExplicitDepsPlugin extends AutoPlugin {
  trait Implicits {
    implicit val moduleFilterRemoveValue: Remove.Value[ModuleFilter, ModuleFilter] =
      new Remove.Value[ModuleFilter, ModuleFilter] {
        override def removeValue(a: ModuleFilter, b: ModuleFilter): ModuleFilter = a - b
      }
  }

  object autoImport extends Implicits {
    val undeclaredCompileDependencies       =
      taskKey[Set[Dependency]]("Find Compile libraries used but not directly declared.")
    val undeclaredCompileDependenciesTest   = taskKey[Unit]("Fail when Compile dependencies are undeclared.")
    val undeclaredCompileDependenciesFilter = settingKey[ModuleFilter]("Filter undeclared dependencies.")
    val unusedCompileDependencies           =
      taskKey[Set[Dependency]]("Find declared Compile dependencies not used by compilation.")
    val unusedCompileDependenciesTest       = taskKey[Unit]("Fail when declared Compile dependencies are unused.")
    val unusedCompileDependenciesFilter     = settingKey[ModuleFilter]("Filter unused dependencies.")
  }

  import autoImport._
  override def trigger              = allRequirements
  override def requires             = plugins.IvyPlugin
  override lazy val projectSettings = Seq(
    undeclaredCompileDependencies       := undeclaredCompileDependenciesTask.value,
    undeclaredCompileDependenciesTest   := undeclaredCompileDependenciesTestTask.value,
    undeclaredCompileDependenciesFilter := DependencyFilter.moduleFilter(),
    unusedCompileDependencies           := unusedCompileDependenciesTask.value,
    unusedCompileDependenciesTest       := unusedCompileDependenciesTestTask.value,
    unusedCompileDependenciesFilter     := DependencyFilter.moduleFilter()
  )

  private lazy val csrCacheDirectoryValueTask = Def.task {
    val extracted = Project.extract(state.value)
    val settings  = extracted.session.original
    settings.find(_.key.key.label == "csrCacheDirectory").map { setting =>
      setting.init.evaluate(extracted.structure.data).toString
    }
  }

  private def usedDependencies(
    analysis: sbt.internal.inc.Analysis,
    scala: ScalaVersion,
    csrCacheDirectory: Option[String],
    baseDirectory: String,
    log: sbt.util.Logger
  ): Set[Dependency] =
    analysis.relations.allLibraryDeps
      .asInstanceOf[Set[AnyRef]]
      .flatMap { ref =>
        BoringStuff.jarFileToDependency(scala, log)(toFile(ref, csrCacheDirectory, baseDirectory))
      }
      .filterNot(dep => Set("scala-library", "scalajs-library", "scala3-library").contains(dep.name))
      .toSet

  private def toFile(ref: AnyRef, csrCacheDirectory: Option[String], baseDirectory: String): java.io.File =
    if (ref.getClass.getSimpleName.contains("VirtualFile")) {
      val id   = ref.getClass.getMethod("id").invoke(ref).toString
      val path = id
        .replace("${CSR_CACHE}", csrCacheDirectory.getOrElse(""))
        .replace("${BASE}", baseDirectory)
      new java.io.File(path)
    } else ref.asInstanceOf[java.io.File]

  private lazy val undeclaredCompileDependenciesTask = Def.task {
    val log               = streams.value.log
    val scala             = ScalaVersion(scalaBinaryVersion.value, scalaVersion.value)
    val analysis          = (Compile / compile).value.asInstanceOf[sbt.internal.inc.Analysis]
    val csrCacheDirectory = csrCacheDirectoryValueTask.value
    val baseDirectory     = appConfiguration.value.baseDirectory().getCanonicalFile.toPath.toString
    Logic.undeclared(
      name.value,
      usedDependencies(analysis, scala, csrCacheDirectory, baseDirectory, log),
      Logic.declared(libraryDependencies.value, log),
      undeclaredCompileDependenciesFilter.value,
      log
    )
  }

  private lazy val undeclaredCompileDependenciesTestTask = Def.task {
    if (undeclaredCompileDependencies.value.nonEmpty) throw UndeclaredCompileDependenciesException
  }

  private lazy val unusedCompileDependenciesTask = Def.task {
    val log               = streams.value.log
    val scala             = ScalaVersion(scalaBinaryVersion.value, scalaVersion.value)
    val analysis          = (Compile / compile).value.asInstanceOf[sbt.internal.inc.Analysis]
    val csrCacheDirectory = csrCacheDirectoryValueTask.value
    val baseDirectory     = appConfiguration.value.baseDirectory().getCanonicalFile.toPath.toString
    Logic.unused(
      name.value,
      usedDependencies(analysis, scala, csrCacheDirectory, baseDirectory, log),
      Logic.declared(libraryDependencies.value, log),
      unusedCompileDependenciesFilter.value,
      log
    )
  }

  private lazy val unusedCompileDependenciesTestTask = Def.task {
    if (unusedCompileDependencies.value.nonEmpty) throw UnusedCompileDependenciesException
  }
}
