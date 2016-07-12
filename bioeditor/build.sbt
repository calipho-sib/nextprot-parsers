name := "nextprot-parser-bed"

organization := "org.nextprot.parsers"

version := "0.0.1-SNAPSHOT"

description := "BioEditor parser used for BioEditor data"

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-feature" )

resolvers += "nexus" at "http://miniwatt:8800/nexus/content/groups/public/"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.oracle.jdbc" % "ojdbc7" % "12.1.0.2",
  "org.apache.jena" % "apache-jena-libs" % "3.0.1",
  "org.nextprot" % "nextprot-commons" % "0.9.0-SNAPSHOT"
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
