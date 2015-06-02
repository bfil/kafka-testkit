import sbt._
import Keys._
import com.bfil.sbt._

object ProjectBuild extends BFilBuild {

  val buildVersion = "0.3.0-SNAPSHOT"

  lazy val root = BFilRootProject("root", file("."))
    .aggregate(embeddedKafka, specs2Kafka)

  lazy val embeddedKafka = BFilProject("embedded-kafka", file("embedded-kafka"))
    .settings(libraryDependencies ++= Dependencies.all(scalaVersion.value))

  lazy val specs2Kafka = BFilProject("specs2-kafka", file("specs2-kafka"))
    .settings(libraryDependencies ++= Dependencies.specs2(scalaVersion.value))
    .dependsOn(embeddedKafka)
}

object Dependencies {
  
  def all(scalaVersion: String) = Seq(
    "org.apache.kafka" %% "kafka" % "0.8.2.1",
    "org.apache.commons" % "commons-io" % "1.3.2",
    "org.apache.curator" % "curator-test" % "2.8.0")

  def specs2(scalaVersion: String) = Seq(
    "org.specs2" %% "specs2-core" % "2.4.17" % "provided")
}