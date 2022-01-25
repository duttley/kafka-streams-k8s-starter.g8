import sbt._

object Dependencies {
  lazy val confluentVersion = "6.0.0"
  lazy val pureConfigVersion = "0.17.1"
  lazy val tapirVersion = "0.17.0-M2"

  lazy val dependencies =
    Seq(
      "io.confluent" % "kafka-avro-serializer" % confluentVersion,
      "org.apache.kafka" % "kafka-clients" % "2.5.0",
      "org.scalatest" %% "scalatest" % "3.1.2" % "it,test",
      "com.google.cloud" % "google-cloud-bigquery" % "1.124.2",
      "org.slf4j" % "slf4j-log4j12" % "1.7.30",
      "org.slf4j" % "slf4j-api" % "1.7.30",
      "log4j" % "log4j" % "1.2.17",
      "com.typesafe" % "config" % "1.4.0",

      "org.typelevel" %% "cats-core" % "2.1.1",

      "com.google.cloud" % "google-cloud-bigquery" % "1.124.2",

      "io.confluent" % "kafka-avro-serializer" % confluentVersion,
      "io.confluent" % "kafka-connect-avro-converter" % confluentVersion,
      "io.confluent" % "kafka-streams-avro-serde" % confluentVersion,
      "io.confluent" % "kafka-json-serializer" % confluentVersion,

      "org.apache.kafka" % "kafka-streams" % "2.6.0",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.6.0",

      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,

      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
    )
}
