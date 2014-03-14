name := "Paramas-scala"

version := "0.1"

scalaVersion := "2.10.3"

atmosSettings

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.0",
  "com.typesafe.akka" %% "akka-agent" % "2.3.0",
  "com.typesafe" % "config" % "0.4.0",
  "ch.qos.logback" % "logback-classic" % "1.0.7"
   )

   
mainClass in (Compile, run) := Some("com.krzywicki.hybrid.HybridApp")
