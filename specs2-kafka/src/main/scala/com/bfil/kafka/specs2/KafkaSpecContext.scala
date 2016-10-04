package com.bfil.kafka.specs2

import com.bfil.kafka.embedded.EmbeddedKafka
import org.specs2.mutable.BeforeAfter

trait EmbeddedKafkaContext extends BeforeAfter { self: KafkaPortsProvider =>
  val kafkaTopics: Set[String]

  lazy val kafka = EmbeddedKafka(port = kafkaPort, zkPort = zkPort)

  def before = {
    kafka.start
    kafka.createTopics(kafkaTopics.toSeq: _*)
  }

  def after = {
    kafka.deleteTopics(kafkaTopics.toSeq: _*)
    kafka.stop
  }
}

trait KafkaPortsProvider {
  val kafkaPort: Int
  val zkPort: Int
}

trait DefaultKafkaPorts extends KafkaPortsProvider {
  val kafkaPort = 9092
  val zkPort = 2181
}

trait RandomKafkaPorts extends KafkaPortsProvider with AvailablePortDiscovery {
  val kafkaPort = randomAvailablePort
  val zkPort = randomAvailablePort
}
