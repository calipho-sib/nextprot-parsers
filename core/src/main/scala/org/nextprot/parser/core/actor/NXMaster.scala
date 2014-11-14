package org.nextprot.parser.core.actor
import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import java.io.File
import java.io.OutputStream
import scala.Array.canBuildFrom
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer
import akka.routing.RoundRobinRouter
import org.nextprot.parser.core.actor.message._
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.NXProperties._
import scala.xml.PrettyPrinter
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.stats.StatisticsCollector
import org.nextprot.parser.core.stats.Stats

/**
 * Master actor responsible to dispatch the files to different parsing actors (NXParserActor) and appends the wrapped beans into a single output file.
 * @author Daniel Teixeira
 */

class NXMaster(nxParserImpl: String, nxReducerImpl: String, files: List[File], listener: ActorRef) extends Actor {

  if (System.getProperty(outputFileProperty) != null) System.getProperty(outputFileProperty) else System.setProperty(outputFileProperty, "output.xml");
  if (System.getProperty(failedFileProperty) != null) System.getProperty(failedFileProperty) else System.setProperty(failedFileProperty, "failed-entries.log");

  private val discardedCases = ArrayBuffer[NXException]();

  val reducer = Class.forName(nxReducerImpl).newInstance().asInstanceOf[org.nextprot.parser.core.NXReducer];

  private var filesCount = 0

  private val count = files.size;
  println("Found " + count + " files! Dispatching files between parsers ...")

  private val workers = Runtime.getRuntime().availableProcessors(); //* 16;
  println("Dispatching files through " + workers + " workers");
  private val workerRouter = context.actorOf(Props[NXWorker].withRouter(RoundRobinRouter(workers)), name = "workerRouter");
  val logFileName = System.getProperty(failedFileProperty)
  private lazy val logFile: FileWriter = new FileWriter(logFileName, false)

  /**
   * Check that all parsers have finished and do the necessary actions to finish the process
   */
  private def checkEnd = {
    if (filesCount == count) {
      end
    }
  }

  private def end = {
    reducer.end
    if (!discardedCases.isEmpty) {
      logFile.close
    }
    listener ! EndActorSystemMSG()
  }

  def receive = {

    case StartParsingMSG => {
      reducer.start
      files.map(f => workerRouter ! ProcessMSG(nxParserImpl, f))
    }

    case m: SuccessFileParsedMSG => {
      reducer.reduce(m.wrapper);
      filesCount += 1
      Stats ++ ("ENTRIES-OUTPUT", "success");
      checkEnd
    }

    case m: NXExceptionFoundMSG => {
      filesCount += 1
      discardedCases += m.exception;
      if (m.exception.getNXExceptionType.isError) {
        Stats.++("ENTRIES-OUTPUT", "ERROR OF TYPE " + m.exception.getNXExceptionType().getClass().getSimpleName());
        logFile.write(m.exception.getFile.getAbsolutePath() + "\n");
        println(m.exception.getFile.getName() + " - " + m.exception.getNXExceptionType.getClass().getSimpleName() + " " + m.exception.getMessage)
      } else {
        Stats ++ ("ENTRIES-OUTPUT", "DISCARDED CASES OF TYPE " + m.exception.getNXExceptionType().getClass().getSimpleName());
      }
      checkEnd
    }

    case _ => {
      println("Unexpected message received ")
      end
    }
  }

}

