import sbt.Keys._
import sbt._
import com.banno.license.Plugin.{licenseSettings => defaultLicenseSettings}
import com.banno.license.Plugin.LicenseKeys._
import com.banno.license.Licenses._

object BuildSettings {

  val buildOrganization = "pl.edu.agh.scalamas"

  val buildVersion = "0.1"

  val buildScalaVersion = "2.11.4"

  val licenseSettings: Seq[Setting[_]] = defaultLicenseSettings ++ Seq(
    license := mit("Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>"),
    removeExistingHeaderBlock := true
  )

  val commonSettings: Seq[Setting[_]] = Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion
  ) ++ licenseSettings
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
