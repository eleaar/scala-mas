import ScalamasSettings._
import Dependencies._

lazy val core = ScalamasSettings.subProject("core")
  .settings(libraryDependencies ++= coreDeps)
  .settings(parallelExecution in Test := false)

lazy val genetic = ScalamasSettings.subProject("genetic")
  .settings(libraryDependencies ++= geneticDeps)
  .dependsOn(core)

lazy val emas = ScalamasSettings.subProject("emas")
  .settings(libraryDependencies ++= emasDeps)
  .dependsOn(core, genetic)

lazy val examples = ScalamasSettings.subProject("examples")
  .dependsOn(emas, core, genetic)

lazy val root = project.in(file("."))
  .enablePlugins(ScalaUnidocPlugin, AutomateHeaderPlugin)
  .settings(commonSettings: _*)
  .settings(PublishSettings.nowhere: _*)
  .aggregate(core, genetic, emas, examples)
