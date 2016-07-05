package com.bfil.kafka.embedded

import java.io.File
import java.util.{Properties, UUID}

import scala.util.{Random, Try}

import org.apache.commons.io.FileUtils
import org.apache.curator.test.TestingServer
import org.apache.log4j.Logger

import kafka.admin.AdminUtils
import kafka.server.{KafkaConfig, KafkaServerStartable}
import kafka.utils.ZkUtils

case class EmbeddedKafka(port: Int = 9092, zkPort: Int = 2181)(implicit val log: Logger = Logger.getLogger("com.bfil.EmbeddedKafka")) {
  private val server = new TestingServer(zkPort, false)
  private val zkUrl = server.getConnectString
  private val logDir = new File(System.getProperty("java.io.tmpdir"), s"embedded-kafka-logs/${UUID.randomUUID.toString}")
  private lazy val zkUtils = ZkUtils(zkUrl, 5000, 5000, false)

  private val props = new Properties()
  props.setProperty("zookeeper.connect", zkUrl)
  props.setProperty("reserved.broker.max.id", "1000000")
  props.setProperty("broker.id", Random.nextInt(1000000).toString)
  props.setProperty("port", s"$port")
  props.setProperty("log.dirs", logDir.getAbsolutePath)
  props.setProperty("delete.topic.enable", "true")
  props.setProperty("auto.create.topics.enable", "false")
  private val kafka = new KafkaServerStartable(new KafkaConfig(props))

  def createTopic(topic: String, partitions: Int = 1, replicationFactor: Int = 1) = {
    AdminUtils.createTopic(zkUtils, topic, partitions, replicationFactor, new Properties)
    while(!topicExists(topic)) Thread.sleep(200)
    log.info(s"Created topic: $topic")
  }

  def createTopics(topics: String*) = topics.foreach(t => createTopic(t))

  def deleteTopic(topic: String) = {
    AdminUtils.deleteTopic(zkUtils, topic)
    while(topicExists(topic)) Thread.sleep(200)
    log.info(s"Deleted topic: $topic")
  }

  def deleteTopics(topics: String*) = topics.foreach(t => deleteTopic(t))

  def topicExists(topic: String) = AdminUtils.topicExists(zkUtils, topic)

  def start = {
    log.info("Starting Kafka..")
    Try {
      server.start
    }
    kafka.startup
    log.info("Kafka started")
  }

  def stop = {
    log.info("Stopping Kafka..")
    kafka.shutdown
    kafka.awaitShutdown
    Try {
      zkUtils.close
      server.close
      server.stop
    }
    FileUtils.deleteDirectory(logDir)
    log.info("Kafka stopped")
  }
}
