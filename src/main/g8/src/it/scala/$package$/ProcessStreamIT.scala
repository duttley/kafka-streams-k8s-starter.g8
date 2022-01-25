package $package$

import java.util
import java.util.{Collections, Properties}
import $package$.testhelpers.Kafka
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import cats.implicits._
import $package$.Main
import $package$.config.Config
import $package$.utils.KafkaUtils
import org.apache.kafka.clients.admin.AdminClient
import org.slf4j.LoggerFactory
import $package$.avro.Address
import collection.JavaConverters._

class ProcessStreamIT extends AnyFlatSpec with Matchers with KafkaProps {
  lazy val config = Config.load()
  lazy val kafkaConfig = config.kafka

  behavior of "ProcessStream"

  it should "read kafka records transform and procude kafka records" in {

    val kafkaProps: Properties = kafkaProperties

    implicit val client = AdminClient.create(kafkaProps)

    KafkaUtils.createTopicIfNotExists(kafkaConfig.topics.inbound, 6, 3)

    val inputData = Address("wef", "wef")

    val expectedOutput = Address("wef", "wef")// Add your expected output here

    val testStream = Main.streams

    try {

      Kafka.writeToKafka(inputData)

      testStream.start()

      val dataFromKafka = Kafka.readFromKafka()

      dataFromKafka should be(expectedOutput)

    } finally {

      testStream.close()
      testStream.cleanUp()
      val topics: util.List[String] =
        List(kafkaConfig.topics.outbound, kafkaConfig.topics.inbound).asJava
      println("Deleting topics")
      client.deleteTopics(topics)
      client.close()
    }

  }

}
