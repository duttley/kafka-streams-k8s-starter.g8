package $package$.testhelpers

import java.time.Duration
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.jdk.CollectionConverters._
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Collections
import $package$.KafkaProps
import $package$.config.Config
import $package$.avro.Address
import $package$.KafkaProps

object Kafka extends KafkaProps {

  lazy val config = Config.load()
  lazy val kafkaConfig = config.kafka

  def writeToKafka(data: Address): Unit = {
    val producer =
      new KafkaProducer[String, Address](kafkaProperties)
    producer
      .send(
        new ProducerRecord[String, Address](
          kafkaConfig.topics.inbound,
          data))
      .isDone
    producer.close()
  }

  def readFromKafka(): Address = {
    val kafkaConsumer =
      new KafkaConsumer[String, Address](kafkaProperties)
    kafkaConsumer.subscribe(
      Collections.singletonList(kafkaConfig.topics.outbound))

    val results = kafkaConsumer.poll(Duration.ofMillis(50000)).asScala
    kafkaConsumer.close
    println()
    results.head.value()
  }
}
