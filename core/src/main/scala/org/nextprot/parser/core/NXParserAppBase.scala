package org.nextprot.parser.core
import java.io.File
import java.io.OutputStream
import scala.io.Source
import scala.Array.canBuildFrom
import java.io.FileWriter
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import org.nextprot.parser.core.actor.message.StartParsingMSG
import org.nextprot.parser.core.actor.NXListener
import org.nextprot.parser.core.actor.NXMaster
import NXProperties._

/**
 * Base class for parsing a proxy directory and create a single output file <br>
 * @author Daniel Teixeira
 */
abstract class NXParserAppBase extends App {

  protected def initialize {

    val parserImpl = getParserImplementation;
    val reducerImpl = getReducerImplementation;
    val files = getFilesForParsing

    if (!(files.isEmpty)) {

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
      println(parserImplementationProperty + " property not found."); System.exit(1); return "";
    } else { Class.forName(System.getProperty(parserImplementationProperty)); return System.getProperty(parserImplementationProperty) };
  }

  private def getReducerImplementation: String = {

    if (System.getProperty(NXProperties.reducerImplementationProperty) == null) {
      println(reducerImplementationProperty + " property not found."); System.exit(1); return "";
    } else { Class.forName(System.getProperty(reducerImplementationProperty)); return System.getProperty(reducerImplementationProperty) };
  }

  private def getFilesForParsing: List[File] = {

    val filterfileName = System.getProperty(InputFilterFileProperty)
    if (filterfileName != null) {
      val filterfile = new File(filterfileName)
      if (!filterfile.exists()) { Console.err.println("The input filter file " + filterfileName + " was not found."); System.exit(1) }
      // Make a list of the content of the file
      println("Taking input data from file " + filterfileName)
      return Source.fromFile(filterfile).getLines.map(f => {
        if (new File(f).exists())
          new File(f)
        else { println(f + " Can't be found"); sys.exit(1) }
      }).toList;
    }

    val directory = System.getProperty(directoryFilesProperty);
    val regularExpression = System.getProperty(regularExpressionProperty);
    if (directory == null) {
      println(directoryFilesProperty + " property not found.");
      System.exit(1)
    }

    // The InputFilterFileProperty has higher priority than regularExpressionProperty
    if (regularExpression == null) {
      println(regularExpressionProperty + " property not found.");
      System.exit(1)
    }

    println("Looking for files like: " + regularExpression + " in directory: " + directory + " ...");

    return filesAt(new File(directory)).filter(f => regularExpression.r.findFirstIn(f.getName).isDefined).toList;
  }

  private def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)

}