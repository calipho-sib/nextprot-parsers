import AssemblyKeys._ // put this at the top of the file

//You can check configuration examples here:
//http://www.scala-sbt.org/release/docs/Examples/Quick-Configuration-Examples

assemblySettings

jarName in assembly := "nextprot-parser-hpa.jar"

mainClass in assembly := Some("org.nextprot.parser.core.HPANXParserApp")

name := "nextprot-parser-hpa"

organization := "org.nextprot.parser.hpa"

version := "0.39.0-SNAPSHOT"

description := "HPA parser used for tissues subcellular locations and antibodies"

scalaVersion := "2.10.4"

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-feature" )

fork := true

// 
// all of them => "-Dfiles.expression=^ENSG.*.xml$",
// 225 samples => "-Dfiles.expression=^ENSG0000000.*.xml$",

// this is read only by sbt tool
// subset of data : -Dfiles.directory=/tmp/hpa-data/ENS/G00/000/001
javaOptions ++= Seq(
"-Xmx2000m",
"-Dfiles.directory=hpa-data",
"-Dfiles.expression=^ENSG.*.xml$",
"-Dhpa.mapping.file=src/test/resources/HPA_Subcell_Mapping.txt",
"-Dhpa.tissue.mapping.file=src/test/resources/NextProt_tissues.from-db.txt",
"-Dhpa.anti.multi.file=src/test/resources/multi_target_antibodies.txt"
)

resolvers += "nexus" at "http://miniwatt:8800/nexus/content/groups/public/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.0",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.7" % "test->default",
  "code.google.com" % "xml-test" % "0.3.0" % "test",
  "org.nextprot.parser.core" % "nextprot-parser-core" % "0.34.+"
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
