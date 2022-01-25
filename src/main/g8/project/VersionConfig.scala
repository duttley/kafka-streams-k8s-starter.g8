import sbt.Keys.version
import sbt.ThisBuild
import sbtrelease.ReleasePlugin.autoImport.{releaseCommitMessage, _}
import sbtrelease.ReleaseStateTransformations._

object VersionConfig {
  val versionConfig = Seq(
    releaseNextCommitMessage := s"Setting next version to \${(version in ThisBuild).value} [ci skip]",
    releaseCommitMessage := s"release-\${(version in ThisBuild).value} [ci skip]",
    releaseIgnoreUntrackedFiles := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies, // : ReleaseStep
      inquireVersions, // : ReleaseStep
      setReleaseVersion, // : ReleaseStep
      commitReleaseVersion, // : ReleaseStep
      pushChanges, // : ReleaseStep, also checks that an upstream branch is properly configured
      setNextVersion, // : ReleaseStep
      commitNextVersion, // : ReleaseStep
      pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
    )
  )
}
