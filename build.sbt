lazy val root = Project("kafka-testkit", file("."))
  .settings(settings, publishArtifact := false)
  .aggregate(embeddedKafka, specs2Kafka)

lazy val embeddedKafka = Project("embedded-kafka", file("embedded-kafka"))
  .settings(settings, libraryDependencies ++= Seq(
    "org.apache.kafka" %% "kafka" % "0.10.2.1" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12"),
    "org.slf4j" % "log4j-over-slf4j" % "1.7.21",
    "org.apache.commons" % "commons-io" % "1.3.2",
    "org.apache.curator" % "curator-test" % "3.2.0"
  ))

lazy val specs2Kafka = Project("specs2-kafka", file("specs2-kafka"))
  .settings(settings, libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "3.8.6" % "provided"
  ))
  .dependsOn(embeddedKafka)

lazy val settings = Seq(
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4", "2.11.12"),
  organization := "io.bfil",
  organizationName := "Bruno Filippone",
  organizationHomepage := Some(url("http://bfil.io")),
  homepage := Some(url("https://github.com/bfil/kafka-testkit")),
  licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),
  developers := List(
    Developer("bfil", "Bruno Filippone", "bruno@bfil.io", url("http://bfil.io"))
  ),
  startYear := Some(2015),
  publishTo := Some("Bintray" at s"https://api.bintray.com/maven/bfil/maven/${name.value}"),
  credentials += Credentials(Path.userHome / ".ivy2" / ".bintray-credentials"),
  scmInfo := Some(ScmInfo(
    url(s"https://github.com/bfil/kafka-testkit"),
    s"git@github.com:bfil/kafka-testkit.git"
  ))
)
