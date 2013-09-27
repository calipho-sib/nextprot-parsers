package org.nextprot.parser.core
import java.io.File
import java.io.OutputStream
import scala.io.Source
import scala.Array.canBuildFrom
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer
import org.nextprot.parser.core.datamodel.AnnotationListWrapper
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import org.nextprot.parser.core.actor.message.StartParsingMSG
import org.nextprot.parser.core.actor.NXListener
import org.nextprot.parser.core.actor.NXMaster
import NXProperties._

/**
 * Main application for parsing a proxy directory and create a single output file <br>
 * Needs at least 3 properties: <br>
 * <ul>
 * <li> parser.impl which is the name of the implementation of the parser. The class must extend [[org.nextprot.parser.core.NXParser]] </li>
 * <li> files.directory which is the directory where the proxy files are located (can be in sub-directories)</li>
 * <li> files.expression defines the type of files that must be parsed </li>
 * </ul>
 *
 * In this example [[org.nextprot.parser.hpa.subcell.HPANXParser]] is the implementation and will parse all files with the regular expression ^ENSG.*\.xml$ located in /tmp/hpa directory:<br>
 * <ul>
 * <li> java -Dparser.impl=org.nextprot.parser.hpa.subcell.HPANXParser -Dfiles.directory=/tmp/hpa-data -Dfiles.expression="""^ENSG.*\.xml$""" -jar nx-parser.jar </li>
 * </ul>
 * <br>
 * <ul>
 * Optionally you can define other properties: <br>
 * <li> output.file (if not defined output.xml will be automatically generated) </li>
 * <li> report.file (if not defined report.log will be automatically generated) </li>
 * </ul>
 *
 * @author Daniel Teixeira
 */
object NXParserApp extends App {

  private val devMode: Boolean = false;
  if (devMode) println("YOU ARE IN DEVELOPMENT MODE!")

  //should this be here?
  private val parserImplementationDefault = "org.nextprot.parser.hpa.subcell.HPANXParser";
  private val directoryFilesDefault = "/tmp/hpa-data";
  private val regularExpressionDefault = """^ENSG.*\.xml$""";


  private val parserImpl = getParserImplementation;

  private val files = getFilesForParsing

  if (!(files.isEmpty)) {

    // Create an Akka system
    val system = ActorSystem("ParsingSystem")
    // create the result listener, which will print the result and shutdown the system

    val listener = system.actorOf(Props[NXListener], name = "listener")
    val master = system.actorOf(Props(new NXMaster(parserImpl, files, listener)), name = "master")

    // start the calculation
    master ! StartParsingMSG

  } else {
    println("Found 0 files")
  }

  private def getParserImplementation: String = {

    if (System.getProperty(NXProperties.parserImplementationProperty) == null) {
      if (devMode) { println("Property \"" + parserImplementationProperty + "\" not set using default: " + parserImplementationDefault + " (dev mode)"); Class.forName(parserImplementationDefault); return parserImplementationDefault }
      else println(parserImplementationProperty + " property not found. Example -D" + parserImplementationProperty + "=\"" + parserImplementationDefault + "\""); System.exit(1); return "";
    } else { Class.forName(System.getProperty(parserImplementationProperty)); return System.getProperty(parserImplementationProperty) };

  }

  private def getFilesForParsing: List[File] = {

    val filterfileName = System.getProperty(InputFilterFileProperty)
    if (filterfileName != null) {
      val filterfile = new File(filterfileName)
      if (!filterfile.exists()) { Console.err.println("The input filter file " + filterfileName + " was not found."); System.exit(1) }
      // Make a list of the content of the file
      println("Taking input data from file " + filterfileName)
      return Source.fromFile(filterfile).getLines.map(f => {
        if(new File(f).exists())
          new File(f) else {println(f + " Can't be found"); sys.exit(1)}
      }).toList;
    }

    val directory = System.getProperty(directoryFilesProperty);
    val regularExpression = System.getProperty(regularExpressionProperty);
    if (directory == null) {
      println(directoryFilesProperty + " property not found. Example -D" + directoryFilesProperty + "=\"" + directoryFilesDefault + "\"");
      System.exit(1)
    }

    // The InputFilterFileProperty has higher priority than regularExpressionProperty
    if (regularExpression == null) {
      println(regularExpressionProperty + " property not found. Example -D" + regularExpressionProperty + "=\"" + regularExpressionDefault + "\"");
      System.exit(1)
    }

    println("Looking for files like: " + regularExpression + " in directory: " + directory + " ...");

    return filesAt(new File(directory)).filter(f => regularExpression.r.findFirstIn(f.getName).isDefined).toList;
  }

  private def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)

}