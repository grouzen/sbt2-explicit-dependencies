/* Copyright 2026 Michael Nedokushev. Licensed under Apache-2.0. */
package explicitdeps

import munit.FunSuite
import sbt.librarymanagement.{ CrossVersion, ModuleID }
import sbt.util.Logger

import java.nio.file.Files

class LogicSuite extends FunSuite:
  private val log = Logger.Null

  test("declared Scala-cross-versioned module retains its identity") {
    val module = ModuleID("org.typelevel", "cats-core", "2.13.0").cross(CrossVersion.binary)
    assertEquals(Logic.declared(Seq(module), log), Set(Dependency("org.typelevel", "cats-core", "2.13.0", true)))
  }

  test("unmatched artifact has no cache-layout identity when no POM or Ivy metadata exists") {
    val jar = Files.createTempFile("explicitdeps-unknown", ".jar").toFile
    try assertEquals(BoringStuff.jarFileToDependency(ScalaVersion("3", "3.8.4"), log)(jar), None)
    finally jar.delete()
  }
