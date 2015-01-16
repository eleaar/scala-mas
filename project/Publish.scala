import sbt.Keys._
import sbt._

object ResolverSettings {
  val nexus = "https://oss.sonatype.org/"
  val publishReleases = "Releases" at nexus + "service/local/staging/deploy/maven2"
  val publishSnapshots = "Snapshots" at nexus + "content/repositories/snapshots"
}

object PublishSettings  {


  /**
   * We actually must publish when doing a publish-local in order to ensure the scala 2.11 build works, very strange
   * things happen if you set publishArtifact := false, since it still publishes an ivy file when you do a
   * publish-local, but that ivy file says there's no artifacts.
   *
   * So, to disable publishing for the 2.11 build, we simply publish to a dummy repo instead of to the real thing.
   */
  def dontPublishSettings = Seq(
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
  )

  def publishSettings: Seq[Setting[_]] = Seq(
    publishArtifact in (Compile, packageSrc) := true,
    publishArtifact in (Test, packageSrc) := false,
    publishTo := {
      Some(if (isSnapshot.value) {
        ResolverSettings.publishSnapshots
      } else {
        ResolverSettings.publishReleases
      })
    },
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    pomExtra := pomExtraXml
  )

  private val pomExtraXml =
    <scm>
      <url>git@github.com:ParaPhraseAGH/scala-mas.git</url>
      <connection>scm:git:git@github.com:ParaPhraseAGH/scala-mas.git</connection>
    </scm> ++ makeDevelopersXml(
      ("Eleaar", "Daniel Krzywicki", "https://github.com/eleaar")
    )

  private def makeDevelopersXml(developers: (String, String, String)*) =
    <developers>
      {
      for ((id, name, url) <- developers) yield
        <developer>
          <id>{id}</id>
          <name>{name}</name>
          <url>{url}</url>
        </developer>
      }
    </developers>
}
