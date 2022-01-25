package $package$.utils

import java.util.Collections
import java.util.concurrent.TimeUnit

import $package$.Topics
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.slf4j.LoggerFactory

object KafkaUtils {
  private val log = LoggerFactory.getLogger(this.getClass)

  def createTopicIfNotExists(
                              topic: String,
                              partitions: Int,
                              replicationFactor: Int
                            )(implicit client: AdminClient): Unit = {
    val newTopic =
      new NewTopic(topic, partitions, replicationFactor.asInstanceOf[Short])
    if (!checkTopicExists(topic)) {
      log.info(s"Creating topic \${topic}")
      client
        .createTopics(Collections.singletonList(newTopic))
        .all()
        .get(10, TimeUnit.SECONDS)
    } else
      log.info(s"Topic \${topic} exists")
  }

  def checkTopicExists(topic: String)(implicit client: AdminClient): Boolean = {
    client
      .listTopics()
      .namesToListings()
      .get(10, TimeUnit.SECONDS)
      .containsKey(topic)
  }

  def manageTopics(topics: Topics)(implicit client: AdminClient): Unit = {
    if (!checkTopicExists(topics.inbound))
      throw new RuntimeException(s"Topic \${topics.inbound} does not exist.")
    createTopicIfNotExists(topics.outbound, 6, 3)
  }
}
