scalaVersion := sys.props("scala.version")
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.13.0" % Provided,
  "org.typelevel" %% "cats-kernel" % "2.13.0" % Optional
)
