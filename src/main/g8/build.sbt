import Dependencies._

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "$package$",
  scalaVersion := "2.13.7",
  scalacOptions ++= Seq(
    "-encoding",
    "utf8",
    "-deprecation",
    "-unchecked"
  )
)

lazy val root: Project = project
  .in(file("."))
  .configs(IntegrationTest)
  .settings(commonSettings)
  .settings(
    name := "$name;format="normalize"$",
    description := "$desc$",
    publish / skip := true,
    fork := true,
    libraryDependencies ++= dependencies
  )
  .settings(Defaults.itSettings)


resolvers ++= Seq(
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Second Typesafe repo" at "https://repo.typesafe.com/typesafe/maven-releases/",
  "Confluent" at "https://packages.confluent.io/maven",
  "jitpack" at "https://jitpack.io",
  Resolver.sonatypeRepo("public")
)

scalacOptions ++= Seq(
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions"
)

sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue

assemblyMergeStrategy in assembly := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case proto if proto.endsWith(".proto") =>
    MergeStrategy.discard
  case info if info.endsWith("module-info.class") =>
    MergeStrategy.discard
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.deduplicate
    }
  case _ => MergeStrategy.deduplicate
}
