package $package$.k8s

import cats.Applicative
import cats.implicits._
import $package$.Topics
import $package$.utils.KafkaUtils._
import org.apache.kafka.clients.admin.AdminClient
import sttp.model.StatusCode
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{endpoint, _}

class HealthChecks[F[_]: Applicative](topics: Topics)(
  implicit val client: AdminClient
) {

  val getHealthReadyEndpoint: ServerEndpoint[Unit, StatusCode, String, Any, F] =
    endpoint.get
      .name("getHealth")
      .in("healthz")
      .in("ready")
      .errorOut(statusCode)
      .out(stringBody)
      .serverLogic { _ =>
        if (checkTopicExists(topics.inbound)
          && checkTopicExists(topics.outbound)) {
          val s: Either[StatusCode, String] = Right("All ready!!")
          s.pure[F]
        } else {
          val s: Either[StatusCode, String] =
            Left(StatusCode.InternalServerError)
          s.pure[F]
        }
      }

  val getHealthLiveEndpoint: ServerEndpoint[Unit, StatusCode, String, Any, F] =
    endpoint.get
      .name("getHealth")
      .in("healthz")
      .in("live")
      .errorOut(statusCode)
      .out(stringBody)
      .serverLogic { _ =>
        val s: Either[StatusCode, String] = Right("Still alive!!")
        s.pure[F]
      }

}
