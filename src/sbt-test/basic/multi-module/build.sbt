ThisBuild / scalaVersion := sys.props("scala.version")

lazy val core = project.settings(libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0")
lazy val tools = project.settings(libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.17")
lazy val root = project.aggregate(core, tools)
