import BuildSettings._
import Dependencies._
import com.banno.license.Licenses._
import com.banno.license.Plugin.LicenseKeys._
import com.banno.license.Plugin.{licenseSettings => defaultLicenseSettings}
import sbt.Keys._
import sbt._
import sbtunidoc.Plugin._

object BuildSettings {

  val buildOrganization = "pl.edu.agh.scalamas"

  val buildVersion = "0.1-SNAPSHOT"

  val buildScalaVersion = "2.11.4"

  val licenseSettings: Seq[Setting[_]] = defaultLicenseSettings ++ Seq(
    licenses +=("MIT", url("http://opensource.org/licenses/MIT")),
    license := mit("Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>"),
    removeExistingHeaderBlock := true
  )

  val commonSettings: Seq[Setting[_]] = Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    homepage := Some(url("http://paraphrase.agh.edu.pl/scala-mas/"))
  ) ++ licenseSettings

  val docsSettings = unidocSettings
}

object ScalaMasBuild extends Build {

  def subProject(name: String) = Project(name, file(name))
    .settings(commonSettings: _*)
    .settings(PublishSettings.sonatype: _*)
    .settings(libraryDependencies ++= runtimeDeps)

  lazy val CoreProject = subProject("core")

  lazy val GeneticProject = subProject("genetic").dependsOn(CoreProject)

  lazy val EmasProject = subProject("emas").dependsOn(CoreProject, GeneticProject)

  lazy val ExamplesProject = subProject("examples").dependsOn(EmasProject, CoreProject, GeneticProject)

  lazy val publishedProjects = Seq[ProjectReference](CoreProject, GeneticProject, EmasProject, ExamplesProject)

  lazy val Root = Project("Root", file("."))
    .settings(commonSettings: _*)
    .settings(PublishSettings.nowhere: _*)
    .settings(docsSettings: _*)
    .aggregate(publishedProjects: _*)
}
