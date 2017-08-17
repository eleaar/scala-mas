import de.heikoseeberger.sbtheader.AutomateHeaderPlugin
import sbt.Keys._
import sbt._

object ScalamasSettings {

  val buildOrganization = "pl.edu.agh.scalamas"

  val buildVersion = "0.2.0-SNAPSHOT"

  val buildScalaVersion = "2.12.3"

  val licenseSettings = Seq(
    organizationName := "Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>",
    startYear := Some(2013),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
  )

  val commonSettings: Seq[Setting[_]] = Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    homepage := Some(url("http://paraphrase.agh.edu.pl/scala-mas/")),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-language:existentials",
      "-Xlint:missing-interpolator"
    )
  ) ++ licenseSettings

  def subProject(name: String) = Project(name, file(name))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(commonSettings: _*)
    .settings(PublishSettings.sonatype: _*)

}
