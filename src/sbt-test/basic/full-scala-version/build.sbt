scalaVersion := "3.8.4"
libraryDependencies += ("org.scala-lang" % "scala3-compiler" % scalaVersion.value).cross(CrossVersion.binary)
