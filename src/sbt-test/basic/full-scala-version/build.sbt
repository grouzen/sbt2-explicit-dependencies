scalaVersion := sys.props("scala.version")
libraryDependencies += {
  if (scalaBinaryVersion.value == "3")
    ("org.scala-lang" % "scala3-compiler" % scalaVersion.value).cross(CrossVersion.binary)
  else
    ("com.lihaoyi" %% "ammonite-interp-api" % "3.0.9").cross(CrossVersion.full)
}
