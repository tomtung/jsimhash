import SonatypeKeys._

sonatypeSettings

name := "jsimhash"

organization := "com.github.tomtung"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.8.2", "2.9.2", "2.9.3", "2.10.4", "2.11.2")

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/tomtung/jsimhash"))

libraryDependencies += "com.google.guava" % "guava" % "18.0"

publishMavenStyle := true

publishArtifact in Test := false

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false}

pomExtra := (
  <scm>
    <url>git@github.com:tomtung/jsimhash.git</url>
    <connection>scm:git:git@github.com:tomtung/jsimhash.git</connection>
  </scm>
  <developers>
    <developer>
      <id>tomtung</id>
      <name>Yubing (Tom) Dong</name>
      <url>http://tomtung.com</url>
    </developer>
  </developers>
)
