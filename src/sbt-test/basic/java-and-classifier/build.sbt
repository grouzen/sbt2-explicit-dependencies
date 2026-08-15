scalaVersion := "3.8.4"
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "2.0.17",
  ("software.amazon.cryptools" % "AmazonCorrettoCryptoProvider" % "1.1.0").classifier("linux-x86_64")
)
