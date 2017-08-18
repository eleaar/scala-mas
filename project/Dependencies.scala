import sbt._

object Dependencies {

  val akkaVersion = "2.5.4"
  val akka = Seq(
    "akka-actor",
    "akka-slf4j",
    "akka-agent",
    "akka-testkit",
    "akka-stream"
  ).map("com.typesafe.akka" %% _ % akkaVersion)

  val config = Seq(
    "com.typesafe" % "config" % "1.3.1",
    "com.iheart" %% "ficus" % "1.4.1"
  )

  val random = Seq("org.apache.commons" % "commons-math3" % "3.6.1")

  val logging = Seq("ch.qos.logback" % "logback-classic" % "1.2.3")

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.0.3",
    "org.mockito" % "mockito-core" % "2.8.47",
    "org.scalamock" %% "scalamock-specs2-support" % "3.6.0",
    "org.scalacheck" %% "scalacheck" % "1.13.5"
  ).map(_ % "test")

  val geneticDeps = config ++ random

  val emasDeps = config ++ logging

  val coreDeps = config ++ random ++ logging ++ akka ++ test
}
