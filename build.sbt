//You can check configuration examples here:
//http://www.scala-sbt.org/release/docs/Examples/Quick-Configuration-Examples

import AssemblyKeys._ // put this at the top of the file

assemblySettings

jarName in assembly := "nextprot-parser-core.jar"

mainClass in assembly := Some("org.nextprot.parser.core.NXParserApp")

name := "nextprot-parser-core"

organization := "org.nextprot.parser.core"

version := "0.8.0-SNAPSHOT"

description := "Nextprot Parser Core"

scalaVersion := "2.10.2"

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-feature" )

fork := true

javaOptions ++= Seq("-Dparser.impl=org.nextprot.parser.core.ParserTest",
"-Dfiles.directory=/tmp/hpa-data",
"-Dfiles.expression=^ENSG.*.xml$"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.0",
  "org.scalatest" % "scalatest_2.10" % "2.0.M7" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.7" % "test->default"
)

// Publish section ////////////////////////////////////////////////////////////////////////////////////////////////////////////
publishTo := {
  val nexus = "http://miniwatt.isb-sib.ch:8800/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "nexus/content/repositories/nextprot-snapshot-repo")
  else
    Some("releases"  at nexus + "nexus/content/repositories/nextprot-repo")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
<licenses>
  <license>
    <name>Apache 2</name>
    <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    <distribution>repo</distribution>
  </license>
</licenses>

crossPaths := false
