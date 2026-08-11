/*
 * Copyright 2018-2023 Chris Birchall
 * Copyright 2026 Michael Nedokushev
 * Licensed under the Apache License, Version 2.0.
 * Modified for this independent sbt 2-only port by Michael Nedokushev, 2026.
 */
package explicitdeps

import java.nio.file.Path
import sbt.*
import sbt.Keys.*
import sbt.librarymanagement.{DependencyFilter, ModuleFilter}

object ExplicitDepsPlugin extends AutoPlugin:
  object autoImport:
    val undeclaredCompileDependencies = taskKey[Set[Dependency]]("Find Compile libraries used but not directly declared.")
    val undeclaredCompileDependenciesTest = taskKey[Unit]("Fail when Compile dependencies are undeclared.")
    val undeclaredCompileDependenciesFilter = settingKey[ModuleFilter]("Filter undeclared dependencies.")
    val unusedCompileDependencies = taskKey[Set[Dependency]]("Find declared Compile dependencies not used by compilation.")
    val unusedCompileDependenciesTest = taskKey[Unit]("Fail when declared Compile dependencies are unused.")
    val unusedCompileDependenciesFilter = settingKey[ModuleFilter]("Filter unused dependencies.")

  import autoImport.*
  override def trigger = allRequirements
  override def requires = plugins.IvyPlugin
  override lazy val projectSettings: Seq[Setting[?]] = Seq(
    undeclaredCompileDependencies := Def.uncached {
      val log = streams.value.log
      Logic.undeclared(name.value, usedDependencies.value, Logic.declared(libraryDependencies.value, log), undeclaredCompileDependenciesFilter.value, log)
    },
    undeclaredCompileDependenciesTest := Def.uncached {
      if undeclaredCompileDependencies.value.nonEmpty then throw UndeclaredCompileDependenciesException
    },
    undeclaredCompileDependenciesFilter := DependencyFilter.moduleFilter(),
    unusedCompileDependencies := Def.uncached {
      val log = streams.value.log
      Logic.unused(name.value, usedDependencies.value, Logic.declared(libraryDependencies.value, log), unusedCompileDependenciesFilter.value, log)
    },
    unusedCompileDependenciesTest := Def.uncached {
      if unusedCompileDependencies.value.nonEmpty then throw UnusedCompileDependenciesException
    },
    unusedCompileDependenciesFilter := DependencyFilter.moduleFilter()
  )

  private lazy val usedDependencies = Def.task {
    val log = streams.value.log
    val converter = fileConverter.value
    val analysis = (Compile / compile).value.asInstanceOf[sbt.internal.inc.Analysis]
    val scala = explicitdeps.ScalaVersion(scalaBinaryVersion.value, scalaVersion.value)
    val modulesByPath = (Compile / externalDependencyClasspath).value.flatMap { entry =>
      entry.get(moduleIDStr).map(Classpaths.moduleIdJsonKeyFormat.read).map(module => converter.toPath(entry.data).normalize -> module)
    }.toMap
    analysis.relations.allLibraryDeps.flatMap { ref =>
      val path = converter.toPath(ref).normalize
      modulesByPath.get(path).map(module => Dependency(module.organization, module.name, module.revision, module.crossVersion.isInstanceOf[sbt.librarymanagement.Binary | sbt.librarymanagement.Full])) match
        case some @ Some(_) => some
        case None =>
          BoringStuff.jarFileToDependency(scala, log)(path.toFile).orElse {
            log.warn(s"Could not classify used compile artifact: $path")
            None
          }
    }.filterNot(dep => Set("scala-library", "scalajs-library", "scala3-library").contains(dep.name)).toSet
  }

object UndeclaredCompileDependenciesException extends FeedbackProvidedException:
  override def toString = "Failing the build because undeclared dependencies were found"
object UnusedCompileDependenciesException extends FeedbackProvidedException:
  override def toString = "Failing the build because unused dependencies were found"
