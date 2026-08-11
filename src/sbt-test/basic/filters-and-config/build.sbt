scalaVersion := "3.8.4"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.slf4j" % "slf4j-api" % "2.0.17",
  "ch.qos.logback" % "logback-classic" % "1.5.18" % Runtime
)
unusedCompileDependenciesFilter -= moduleFilter("org.slf4j", "slf4j-api")
