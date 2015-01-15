import sbt.Keys._
import sbt._

object BuildSettings {

  val buildOrganization = "org.scalamas"

  val buildVersion = "0.1"

  val buildScalaVersion = "2.11.4"

  val commonSettings: Seq[Setting[_]] = Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

object ScalaMasBuild extends Build {

  import BuildSettings._
  import Dependencies._

  def subProject(name: String) = Project(name, file(name))
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= runtimeDeps)

  lazy val CoreProject = subProject("core")

  lazy val GeneticProject = subProject("genetic").dependsOn(CoreProject)

  lazy val EmasProject = subProject("emas").dependsOn(CoreProject, GeneticProject)

  lazy val ExamplesProject = subProject("examples").dependsOn(EmasProject, CoreProject, GeneticProject)
}
