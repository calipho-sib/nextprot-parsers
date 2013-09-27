package org.nextprot.parser.core.actor
import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import java.io.File
import java.io.OutputStream
import scala.Array.canBuildFrom
import org.nextprot.parser.core.datamodel.AnnotationListWrapper
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer
import org.nextprot.parser.core.datamodel.AnnotationListWrapper
import akka.routing.RoundRobinRouter
import org.nextprot.parser.core.actor.message._
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.NXProperties._
import scala.xml.PrettyPrinter

/**
 * Master actor responsible to dispatch the files to different parsing actors (NXParserActor) and appends the wrapped beans into a single output file.
 * @author Daniel Teixeira
 */

class NXMaster(nxParserImpl: String, files: List[File], listener: ActorRef) extends Actor {

  if (System.getProperty(outputFileProperty) != null) System.getProperty(outputFileProperty) else System.setProperty(outputFileProperty, "output.xml");
  if (System.getProperty(failedFileProperty) != null) System.getProperty(failedFileProperty) else System.setProperty(failedFileProperty, "failed-entries.log");

  private val errors = ArrayBuffer[NXException]();

  private var success = 0;
  private var filesCount = 0

  private val count = files.size;
  println("Found " + count + " files! Dispatching files between parsers ...")

  private val processors = Runtime.getRuntime().availableProcessors();
  println("Dispatching files through " + processors + " workers");
  private val workerRouter = context.actorOf(Props[NXWorker].withRouter(RoundRobinRouter(processors)), name = "workerRouter");
  private val fw = new FileWriter(System.getProperty("output.file"), false)
  val logFileName = System.getProperty(failedFileProperty)
  private lazy val logFile: FileWriter = new FileWriter(logFileName, false)
  private val prettyPrinter = new PrettyPrinter(1000, 4);

  /**
   * Check that all parsers have finished and do the necessary actions to finish the process
   */
  private def checkEnd = {
    if (filesCount == count) {
      end
    }
  }

  private def end = {
    if (!errors.isEmpty) {
      logFile.close
    }
    fw.write("</object-stream>");
    fw.close;
    listener ! EndActorSystemMSG(success, errors, files)
  }

  def receive = {
    case StartParsingMSG => {
      fw.write("<object-stream>\n")
      files.map(f => workerRouter ! ParseFileMSG(nxParserImpl, f))
    }
    case m: SuccessFileParsedMSG => {
      filesCount += 1
      success += 1
      fw.write(prettyPrinter.format(m.wrapper.toXML) + "\n")
      checkEnd
    }
    case m: NXExceptionFoundMSG => {
      filesCount += 1
      errors += m.exception;
      if (m.exception.getNXExceptionType.isError) {
        logFile.write(m.file.getAbsolutePath() + "\n");
        println(m.file.getName() + " - " + m.exception.getNXExceptionType.getClass().getSimpleName() + " " + m.exception.getMessage)
      }
      checkEnd
    }
    case _ => {
      println("Unexpected message received ")
      end
    }
  }

}

