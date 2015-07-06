package org.nextprot.parser.core
import java.io.File
import scala.io.Source
import scala.Array.canBuildFrom
import akka.actor.ActorSystem
import akka.actor.Props
import org.nextprot.parser.core.actor.message.StartParsingMSG
import org.nextprot.parser.core.actor.NXListener
import org.nextprot.parser.core.actor.NXMaster
import NXProperties._

/**
 * Base class for parsing a proxy directory and create a single output file <br>
 * @author Daniel Teixeira
 */
abstract class NXParserAppBase extends App {

  protected def initialize() {

    val parserImpl = getParserImplementation
    val reducerImpl = getReducerImplementation
    val files = getFilesForParsing

    if (files.nonEmpty) {

      // Create an Akka system
      val system = ActorSystem("ParsingSystem")
      // create the result listener, which will print the result and shutdown the system

      val listener = system.actorOf(Props[NXListener], name = "listener")
      val master = system.actorOf(Props(new NXMaster(parserImpl, reducerImpl, files, listener)), name = "master")

      master ! StartParsingMSG

    } else {
      println("Found 0 files")
    }

    println("Done")
  }

  private def getParserImplementation: String = {

    if (System.getProperty(NXProperties.parserImplementationProperty) == null) {
      println(parserImplementationProperty + " property not found."); System.exit(1); ""
    } else { Class.forName(System.getProperty(parserImplementationProperty)); System.getProperty(parserImplementationProperty) }
  }

  private def getReducerImplementation: String = {

    if (System.getProperty(NXProperties.reducerImplementationProperty) == null) {
      println(reducerImplementationProperty + " property not found."); System.exit(1); ""
    } else { Class.forName(System.getProperty(reducerImplementationProperty)); System.getProperty(reducerImplementationProperty) }
  }

  private def getFilesForParsing: List[File] = {

    val directory = System.getProperty(directoryFilesProperty)
    val regularExpression = System.getProperty(regularExpressionProperty)
    if (directory == null) {
      println(directoryFilesProperty + " property not found.")
      System.exit(1)
    }

    // The InputFilterFileProperty has higher priority than regularExpressionProperty
    if (regularExpression == null) {
      println(regularExpressionProperty + " property not found.")
      System.exit(1)
    }

    println("Looking for files like: " + regularExpression + " in directory: " + directory + " ...")

    val files = filesAt(new File(directory)).filter(f => regularExpression.r.findFirstIn(f.getName).isDefined).toList

    val filterfileName = System.getProperty(InputFilterFileProperty)

    // filter files by accession number for parsing
    if (filterfileName != null) {

      val filterfile = new File(filterfileName)
      if (!filterfile.exists()) { Console.err.println("The input filter file " + filterfileName + " was not found."); System.exit(1) }
      println("Filtering files with accessions number defined in file ", filterfileName, " ...")

      // get list of uncommented file names to parse
      val accessions:List[String] = Source.fromFile(filterfile).getLines().filterNot(line => line.charAt(0) == '#').map(line => line +".xml").toList

      return files.filter(file => accessions.contains(file.getName))
    }

    files
  }

  private def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)
}