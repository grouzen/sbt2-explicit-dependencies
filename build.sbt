// Copyright 2026 Michael Nedokushev
// SPDX-License-Identifier: Apache-2.0

enablePlugins(SbtPlugin)

organization := "me.mnedokushev"
name := "sbt2-explicit-dependencies"
description := "An independently maintained sbt 2-only port of sbt-explicit-dependencies"
homepage := Some(uri("https://github.com/grouzen/sbt2-explicit-dependencies"))
licenses := List("Apache-2.0" -> uri("https://www.apache.org/licenses/LICENSE-2.0.txt"))
developers := List(
  Developer(
    "mnedokushev",
    "Michael Nedokushev",
    "michael.nedokushev@gmail.com",
    uri("https://github.com/mnedokushev")
  )
)

// This port deliberately targets the sbt 2 API only. SbtPlugin supplies the
// Scala 3 version used by the selected sbt baseline.
sbtVersion := "2.0.4"
semanticdbEnabled := true

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.4.0"
libraryDependencies += "org.scalameta" %% "munit" % "1.2.4" % Test

scriptedLaunchOpts ++= Seq(
  "-Xmx1024M",
  "-Dplugin.version=" + version.value,
  s"-Dsbt.boot.directory=${file(sys.props("user.home")) / ".sbt" / "boot"}"
)
scriptedBufferLog := false
