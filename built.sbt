organization := "pl.edu.agh"

name := "paraphrase-scala"

version := "0.1"

scalaVersion := "2.11.2"

val akkaVersion = "2.3.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe" % "config" % "1.2.1",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.2" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.scalamock" %% "scalamock-specs2-support" % "3.1.2" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.5" % "test"
   )
