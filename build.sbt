name := "simpleCrawler"

version := "0.1"

scalaVersion := "2.12.8"

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.6",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.22.0",
  "org.jsoup" % "jsoup" % "1.11.3"
)