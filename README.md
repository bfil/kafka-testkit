Kafka Testkit
=============

A Scala library that provides an easily embeddable Kafka server for testing and prototyping ([embedded-kafka](https://github.com/bfil/kafka-testkit/tree/master/embedded-kafka)).

A separate module provides an easy integration with specs2 ([specs2-kafka](https://github.com/bfil/kafka-testkit/tree/master/specs2-kafka)).

Setting up the dependencies
---------------------------

__Kafka Testkit__ is available at my [Nexus Repository](http://nexus.b-fil.com/nexus/content/groups/public/), and it is cross compiled and published for both Scala 2.10 and 2.11.

Using SBT, add the following dependencies to your build file

If you only need Embedded Kafka:

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "embedded-kafka" % "0.5.0"
)
```

To also get the helpers for Specs2:

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "specs2-kafka" % "0.5.0"
)
```

Don't forget to add the following resolver:

```scala
resolvers += "BFil Nexus Releases" at "http://nexus.b-fil.com/nexus/content/repositories/releases/"
```

Choose the version based on your target Kafka version

- For Kafka `0.10.0.0` use `>=0.5.0`
- For Kafka `0.9.0.1` use `>=0.3.0` and `<=0.4.0`
- For Kafka `0.8.2.1` use `<=0.2.0`

*APIs have changed between versions, so the examples below might only work for recent versions*

### Using snapshots

If you need a snapshot dependency:

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "embedded-kafka" % "0.6.0-SNAPSHOT"
)

libraryDependencies ++= Seq(
  "com.bfil" %% "specs2-kafka" % "0.6.0-SNAPSHOT"
)

resolvers += "BFil Nexus Snapshots" at "http://nexus.b-fil.com/nexus/content/repositories/snapshots/"
```

Usage
-----

### Embedded Kafka

EmbeddedKafka has a very simple API:

```scala
// Creates an instance of EmbeddedKafka
val kafka = EmbeddedKafka(port = 9092, zkPort = 2181)

// Starts the embedded server (both ZooKeeper and the Kafka broker):
kafka.start

// Creates topics needed
kafka.createTopic("test")
// or
kafka.createTopics("test", "another-test", ...)

// Deletes topics (not usually needed, and also a bit buggy at times due to the asynchronous nature of topics deletion in Kafka)
kafka.deleteTopic("test")
// or
kafka.deleteTopics("test", "another-test", ...)

// Checks if a topic exists
kafka.topicExists("test")

// Stops the embedded server (both ZooKeeper and the Kafka broker):
kafka.stop
```

### Specs2 Kafka

Specs2 Kafka provides a basic trait to scope each test with its own Embedded Kafka.

Tests must be run using the sequential keyword of Specs2.

Extend `EmbeddedKafkaContext` to create a test context that starts up and stops an Embedded Kafka server before and after the test (it also manages the topics creation/deletion), like so:

```scala
trait EmbeddedKafka extends EmbeddedKafkaContext with DefaultKafkaPorts {
  val kafkaTopics = Set("test", "another-test")
}
```

It can be configured to use random available ports by mixing in `RandomKafkaPorts` instead of `DefaultKafkaPorts`:

```scala
trait EmbeddedKafka extends EmbeddedKafkaContext with RandomKafkaPorts {
  val kafkaTopics = Set("test", "another-test")
}
```

Then use it in your tests:

```scala
"my test" in new EmbeddedKafka {
  // ...
}
```
