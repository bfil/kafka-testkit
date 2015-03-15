package com.bfil.kafka.specs2

import org.specs2.mutable.BeforeAfter

import com.bfil.kafka.embedded.EmbeddedKafka

trait EmbeddedKafkaContext extends BeforeAfter {
  val topics: Set[String]

  val kafka = EmbeddedKafka()
  kafka.start

  def before = kafka.createTopics(topics.toSeq: _*)

  def after = {
    kafka.deleteTopics(topics.toSeq: _*)
    kafka.stop
  }
}