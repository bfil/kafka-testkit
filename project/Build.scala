import sbt._
import Keys._
import com.bfil.sbt._

object ProjectBuild extends BFilBuild {

  val buildVersion = "0.6.0-SNAPSHOT"

  lazy val root = BFilRootProject("kafka-testkit", file("."))
    .aggregate(embeddedKafka, specs2Kafka)

  lazy val embeddedKafka = BFilProject("embedded-kafka", file("embedded-kafka"))
    .settings(scalaVersion := "2.11.8")
    .settings(libraryDependencies ++= Dependencies.all(scalaVersion.value))

  lazy val specs2Kafka = BFilProject("specs2-kafka", file("specs2-kafka"))
    .settings(scalaVersion := "2.11.8")
    .settings(libraryDependencies ++= Dependencies.specs2(scalaVersion.value))
    .dependsOn(embeddedKafka)
}

object Dependencies {

  def all(scalaVersion: String) = Seq(
    "org.apache.kafka" %% "kafka" % "0.10.0.0" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12"),
    "org.slf4j" % "log4j-over-slf4j" % "1.7.21",
    "org.apache.commons" % "commons-io" % "1.3.2",
    "org.apache.curator" % "curator-test" % "3.2.0")

  def specs2(scalaVersion: String) = Seq(
    "org.specs2" %% "specs2-core" % "3.8.4" % "provided")
}
