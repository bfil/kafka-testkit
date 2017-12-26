Kafka Testkit
=============

A Scala library that provides an easily embeddable Kafka server for testing and prototyping ([embedded-kafka](https://github.com/bfil/kafka-testkit/tree/master/embedded-kafka)).

A separate module provides an easy integration with specs2 ([specs2-kafka](https://github.com/bfil/kafka-testkit/tree/master/specs2-kafka)).

Setting up the dependencies
---------------------------

__Kafka Testkit__ is available on `Maven Central` (since version `0.8.0`), and it is cross compiled and published for Scala 2.12 and 2.11.

*Older artifacts versions are not available anymore due to the shutdown of my self-hosted Nexus Repository in favour of Bintray*

Using SBT, add the following dependencies to your build file

If you only need Embedded Kafka:

```scala
libraryDependencies ++= Seq(
  "io.bfil" %% "embedded-kafka" % "0.8.0"
)
```

To also get the helpers for Specs2:

```scala
libraryDependencies ++= Seq(
  "io.bfil" %% "specs2-kafka" % "0.8.0"
)
```

If you have issues resolving the dependency, you can add the following resolver:

```scala
resolvers += Resolver.bintrayRepo("bfil", "maven")
```

Choose the version based on your target Kafka version

- For Kafka `0.10.2.1` use `>=0.8.0`
- For Kafka `0.10.0.1` use `>=0.6.0` and `<=0.7.0`
- For Kafka `0.10.0.0` use `>=0.5.0` and `<=0.5.0`
- For Kafka `0.9.0.1` use `>=0.3.0` and `<=0.4.0`
- For Kafka `0.8.2.1` use `<=0.2.0`

*APIs have changed between versions, so the examples below might only work for recent versions*

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

License
-------

This software is licensed under the Apache 2 license, quoted below.

Copyright © 2015-2017 Bruno Filippone <http://bfil.io>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    [http://www.apache.org/licenses/LICENSE-2.0]

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
