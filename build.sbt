// Copyright 2026 Michael Nedokushev
// SPDX-License-Identifier: Apache-2.0

enablePlugins(SbtPlugin)

val scala212 = "2.12.21"
val scala3   = "3.8.4"

crossScalaVersions := Seq(scala212, scala3)
scalaVersion       := scala212

(pluginCrossBuild / sbtVersion) := {
  if (scalaVersion.value.startsWith("2.12.")) "1.3.13"
  else "2.0.4"
}

organization := "me.mnedokushev"
name         := "sbt2-explicit-dependencies"
description  := "An independently maintained port of sbt-explicit-dependencies"
homepage     := Some(url("https://github.com/grouzen/sbt2-explicit-dependencies"))
scmInfo      := Some(
  ScmInfo(
    url("https://github.com/grouzen/sbt2-explicit-dependencies"),
    "scm:git:https://github.com/grouzen/sbt2-explicit-dependencies.git",
    Some("scm:git:git@github.com:grouzen/sbt2-explicit-dependencies.git")
  )
)
licenses     := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
developers   := List(
  Developer(
    "mnedokushev",
    "Michael Nedokushev",
    "michael.nedokushev@gmail.com",
    url("https://github.com/mnedokushev")
  )
)

semanticdbEnabled := true

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.4.0"
libraryDependencies += "org.scalameta"          %% "munit"     % "1.2.4" % Test

scriptedLaunchOpts ++= Seq(
  "-Xmx1024M",
  "-Dplugin.version=" + version.value,
  "-Dscala.version=" + scalaVersion.value,
  s"-Dsbt.boot.directory=${file(sys.props("user.home")) / ".sbt" / "boot"}"
)
scriptedBufferLog := false
