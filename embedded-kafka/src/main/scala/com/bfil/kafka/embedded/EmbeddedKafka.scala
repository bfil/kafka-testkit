package com.bfil.kafka.embedded

import java.io.File
import java.util.{ Properties, UUID }

import org.I0Itec.zkclient.ZkClient
import org.apache.commons.io.FileUtils
import org.apache.curator.test.TestingServer
import org.apache.log4j.{ Level, Logger }

import kafka.admin.AdminUtils
import kafka.server.{ KafkaConfig, KafkaServerStartable }
import kafka.utils.ZKStringSerializer

case class EmbeddedKafka(port: Int = 9092, zkPort: Int = 2181, logLevel: Level = Level.ERROR)(implicit val log: Logger = Logger.getRootLogger) {

  Logger.getLogger("org").setLevel(logLevel)
  Logger.getLogger("kafka").setLevel(logLevel)

  // Silencing Kafka network errors
  Logger.getLogger("kafka.network.Processor").setLevel(Level.OFF)

  private val server = new TestingServer(zkPort, false)
  private val zkConnectionString = server.getConnectString
  private val logDir = new File(System.getProperty("java.io.tmpdir"), s"kafka-test-logs/${UUID.randomUUID.toString}")
  private lazy val zkClient = new ZkClient(zkConnectionString, 5000, 5000, ZKStringSerializer)

  private val props = new Properties()
  props.setProperty("zookeeper.connect", zkConnectionString)
  props.setProperty("broker.id", "0")
  props.setProperty("port", s"$port")
  props.setProperty("log.dirs", logDir.getAbsolutePath)
  props.setProperty("delete.topic.enable", "true")
  private val kafka = new KafkaServerStartable(new KafkaConfig(props))

  def createTopic(topic: String, partitions: Int = 1, replicationFactor: Int = 1) = {
    AdminUtils.createTopic(zkClient, topic, partitions, replicationFactor, new Properties)
    while (!topicExists(topic)) Thread.sleep(200)
    log.info(s"Created topic: $topic")
  }

  def createTopics(topics: String*) = topics.foreach(t => createTopic(t))

  def deleteTopic(topic: String) = {
    AdminUtils.deleteTopic(zkClient, topic)
    // Waiting for a topic to be deleted does not always complete
    // while(topicExists(topic)) Thread.sleep(200)
    log.info(s"Deleted topic: $topic")
  }

  def deleteTopics(topics: String*) = topics.foreach(t => deleteTopic(t))

  def topicExists(topic: String) = AdminUtils.topicExists(zkClient, topic)

  def start = {
    log.info("Starting Kafka..")
    server.start
    kafka.startup
    log.info("Kafka started")
  }

  def stop = {
    log.info("Stopping Zookeeper client..")
    try {
      zkClient.close
    } catch {
      case ex: Throwable => ()
    }
    log.info("Stopping Kafka..")
    kafka.shutdown
    kafka.awaitShutdown
    // Zookeeper testing server can throw exceptions when closed/stopped
    try {
      server.close
      server.stop
    } catch {
      case ex: Throwable => ()
    }
    FileUtils.deleteDirectory(logDir)
    log.info("Kafka stopped")
  }
}