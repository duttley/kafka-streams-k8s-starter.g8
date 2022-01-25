package $package$
import java.time.Duration
import java.util.Date

import cats.effect.{ExitCode, IO, IOApp}
import $package$.config.Config
import $package$.k8s.HealthChecks
import $package$.KafkaProps
import $package$.avro.{Address, AddressEnriched}
import $package$.model.AddressEnrichedTransform
import fs2.Stream
import $package$.utils.KafkaUtils.manageTopics
import io.confluent.connect.avro.AvroData
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import org.slf4j.LoggerFactory
import sttp.tapir.server.http4s._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp with KafkaProps {
  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._
  import java.util.Collections
  import java.util

  private lazy val config = Config.load()
  lazy val kafkaConfig: KafkaConfig = config.kafka

  implicit val inputSerde = new SpecificAvroSerde[Address] {
    configure(kafkaMap, false)
  }

  implicit val outputSerde = new SpecificAvroSerde[AddressEnriched] {
    configure(kafkaMap, false)
  }

  private val log = LoggerFactory.getLogger(this.getClass)

  implicit val client: AdminClient = AdminClient.create(kafkaProperties)
  manageTopics(kafkaConfig.topics, kafkaConfig.topics.repFactor)

  val builder = new StreamsBuilder()

  val ob: KStream[String, Address] =
    builder.stream[String, Address](kafkaConfig.topics.inbound)
  val ob2: KStream[String, AddressEnriched] = ob.map { (key, value) =>
    println(s"Received message @ \${new Date().getTime}")
    println("Key: " + key)
    println("Val: " + value)
    (key, AddressEnrichedTransform.from(value))
  }

  ob2.to(kafkaConfig.topics.outbound)

  val streams: KafkaStreams = new KafkaStreams(builder.build(), kafkaProperties)

  override def run(args: List[String]): IO[ExitCode] = {
    val heathEndpoints = new HealthChecks[IO](kafkaConfig.topics)
    val server = for {
      _ <- Stream.eval(IO(streams.start()))
      server <- BlazeServerBuilder[IO](global)
        .bindHttp(8080, "localhost")
        .withHttpApp(
          Router(
            "/" -> heathEndpoints.getHealthReadyEndpoint.toRoutes,
            "/" -> heathEndpoints.getHealthLiveEndpoint.toRoutes
          ).orNotFound
        )
        .serve
    } yield server

    server.compile.lastOrError
  }
}
