import ScalamasSettings._
import sbt.Keys._

lazy val core = ScalamasSettings.subProject("core")
  .settings(libraryDependencies ++= Dependencies.coreDeps)
  .settings(parallelExecution in Test := false)

lazy val genetic = ScalamasSettings.subProject("genetic")
  .settings(libraryDependencies ++= Dependencies.geneticDeps)
  .dependsOn(core)

lazy val emas = ScalamasSettings.subProject("emas")
  .settings(libraryDependencies ++= Dependencies.emasDeps)
  .dependsOn(core, genetic)

val ContinuousStreamingApp = config("ContinuousStreamingApp") extend Compile
val SequentialStreamingApp = config("SequentialStreamingApp") extend Compile
val SynchronousStreamingApp = config("SynchronousStreamingApp") extend Compile

val assemblyAll = TaskKey[Seq[File]]("assemblyAll", "Assembly many jars")

def configAssembly(configuration: Configuration) = inConfig(configuration)(
  baseAssemblySettings ++ Seq(
  mainClass in assembly := Some(s"pl.edu.agh.scalamas.examples.${configuration.name}"),
  assemblyJarName in assembly := s"${configuration.name}.jar"
)) ++ Seq(
  assemblyAll += (assembly in configuration).value,
)

lazy val examples: Project = ScalamasSettings.subProject("examples")
  .configs(ContinuousStreamingApp, SequentialStreamingApp, SynchronousStreamingApp)
  .dependsOn(emas, core, genetic)
  .settings(
    test in assembly := {},
    assemblyAll := Nil,
    assemblyMergeStrategy in assembly := {
      case "logback.conf" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    configAssembly(ContinuousStreamingApp),
    configAssembly(SequentialStreamingApp),
    configAssembly(SynchronousStreamingApp)
  )

lazy val root = project.in(file("."))
  .enablePlugins(ScalaUnidocPlugin, AutomateHeaderPlugin)
  .settings(commonSettings: _*)
  .settings(PublishSettings.nowhere: _*)
  .aggregate(core, genetic, emas, examples)
