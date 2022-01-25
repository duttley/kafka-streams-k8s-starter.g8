package $package$

import java.util.Properties
import java.util

import io.confluent.kafka.serializers.{
  AbstractKafkaAvroSerDeConfig,
  AbstractKafkaSchemaSerDeConfig,
  KafkaAvroDeserializer,
  KafkaAvroDeserializerConfig,
  KafkaAvroSerializer
}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.{
  StringDeserializer,
  StringSerializer
}

case class SchemaRegistry(url: String, auth: String)
case class Topics(inbound: String, outbound: String, repFactor: Int)
case class Sasl(user: String, password: String)
case class KafkaConfig(id: String,
                       group: String,
                       topics: Topics,
                       bootstrapServer: String,
                       requestTimeoutMs: Int,
                       retryBackoffMs: Int,
                       schemaRegistry: SchemaRegistry,
                       sasl: Sasl)

trait KafkaProps {

  val kafkaConfig: KafkaConfig

  val kafkaProperties: Properties = {

    val p = new Properties()
    //    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "demo-application" + Calendar.getInstance.getTimeInMillis)
    p.put("application.id", kafkaConfig.id)
    p.put("group.id", kafkaConfig.group)


    p.put("bootstrap.servers", kafkaConfig.bootstrapServer)
    p.put("request.timeout.ms", kafkaConfig.requestTimeoutMs)
    p.put("retry.backoff.ms", "500")
    if(kafkaConfig.sasl.user != null && kafkaConfig.sasl.user.nonEmpty){
      p.put("ssl.endpoint.identification.algorithm", "https")
      p.put("sasl.mechanism", "PLAIN")
      p.put("security.protocol", "SASL_SSL")
      p.put(
        "sasl.jaas.config",
        s"""org.apache.kafka.common.security.plain.PlainLoginModule required username="${kafkaConfig.sasl.user}" password="${kafkaConfig.sasl.password}";"""
      )
    }

    if(kafkaConfig.schemaRegistry.auth != null && kafkaConfig.schemaRegistry.auth.nonEmpty){
      p.put("basic.auth.credentials.source", "USER_INFO")
      p.put("basic.auth.user.info", kafkaConfig.schemaRegistry.auth)
    }
    p.put(
      AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
      kafkaConfig.schemaRegistry.url
    )

    p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    p.put(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      classOf[KafkaAvroSerializer]
    )
    p.put(
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      classOf[StringDeserializer]
    )
    p.put(
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      classOf[KafkaAvroDeserializer]
    )
    p.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
    // helps for debug (not optimal for prod): makes KTable data immediately available, without de-duplications
    //    p.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0")
    p
  }

  val kafkaMap: util.Map[String, String] =
    kafkaProperties.asInstanceOf[util.Map[String, String]]

}
