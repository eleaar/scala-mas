name := "Paramas-scala"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.2.3",
  "com.typesafe.akka" %% "akka-agent" % "2.2.3",
   "ch.qos.logback" % "logback-classic" % "1.0.7"
   )
