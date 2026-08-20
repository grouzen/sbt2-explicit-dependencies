/*
 * Copyright 2018-2023 Chris Birchall
 * Copyright 2026 Michael Nedokushev
 * Licensed under the Apache License, Version 2.0.
 * Modified for this independent sbt port by Michael Nedokushev, 2026.
 */
package explicitdeps

import sbt.util.Logger

import java.io.File
import scala.util.control.NonFatal
import scala.xml.XML

final case class ScalaVersion(binary: String, full: String)

/** Cache-layout recovery for classpath entries that lack sbt module metadata. */
object BoringStuff {
  def jarFileToDependency(scalaVersion: ScalaVersion, log: Logger)(jar: File): Option[Dependency] =
    findPomFile(jar)
      .flatMap(parsePomFile(scalaVersion, log))
      .orElse(findIvyFileInIvyCache(jar).flatMap(parseIvyFile(scalaVersion, log)))
      .orElse(findIvyFileInIvyLocal(jar).flatMap(parseIvyFile(scalaVersion, log)))

  private def findPomFile(jar: File): Option[File] =
    jar.getName
      .dropRight(4)
      .split('-')
      .inits
      .filter(_.nonEmpty)
      .map(_.mkString("-") + ".pom")
      .map(name => new File(jar.getParentFile, name))
      .find(_.exists)

  private def findIvyFileInIvyCache(jar: File): Option[File] = {
    val parts = jar.getName.dropRight(4).split('-').drop(1)
    (parts.tails.toList.reverse ++ parts.inits.toList.tail)
      .filter(_.nonEmpty)
      .map(parts => new File(jar.getParentFile.getParentFile, s"ivy-${parts.mkString("-")}.xml"))
      .find(_.exists)
  }

  private def findIvyFileInIvyLocal(jar: File): Option[File] =
    Some(new File(jar.getParentFile.getParentFile, "ivys/ivy.xml")).filter(_.exists)

  private def parsePomFile(scalaVersion: ScalaVersion, log: Logger)(file: File): Option[Dependency] =
    try {
      val xml           = XML.loadFile(file)
      val organization  = Option((xml \ "groupId").text).filter(_.nonEmpty).getOrElse((xml \ "parent" \ "groupId").text)
      val (name, cross) = parseModuleName(scalaVersion)((xml \ "artifactId").text)
      Some(Dependency(organization, name, file.getParentFile.getName, cross))
    } catch {
      case NonFatal(_) =>
        log.warn(s"Failed to parse dependency information from POM file ${file.getAbsolutePath}"); None
    }

  private def parseIvyFile(scalaVersion: ScalaVersion, log: Logger)(file: File): Option[Dependency] =
    try {
      val xml           = XML.loadFile(file)
      val (name, cross) = parseModuleName(scalaVersion)((xml \ "info" \@ "module"))
      Some(Dependency(xml \ "info" \@ "organisation", name, xml \ "info" \@ "revision", cross))
    } catch {
      case NonFatal(_) =>
        log.warn(s"Failed to parse dependency information from Ivy file ${file.getAbsolutePath}"); None
    }

  private def parseModuleName(scalaVersion: ScalaVersion)(raw: String): (String, Boolean) =
    if (raw.endsWith(s"_${scalaVersion.binary}")) (raw.stripSuffix(s"_${scalaVersion.binary}"), true)
    else if (raw.endsWith(s"_${scalaVersion.full}")) (raw.stripSuffix(s"_${scalaVersion.full}"), true)
    else (raw, false)
}
