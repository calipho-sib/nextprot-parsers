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

/**
 * Master actor responsible to dispatch the files to different parsing actors (NXParserActor) and appends the wrapped beans into a single output file.
 * @author Daniel Teixeira
 */

class NXMaster(nxParserImpl: String, files: List[File], listener: ActorRef) extends Actor {

  
  if (System.getProperty(outputFileProperty) != null) System.getProperty(outputFileProperty) else System.setProperty(outputFileProperty, "output.xml");
  if (System.getProperty(failedFileProperty) != null) System.getProperty(failedFileProperty) else System.setProperty(failedFileProperty, "failed-entries.log");

  private val discardedCases = ArrayBuffer[NXException]();

  private var success = 0;
  private var filesCount = 0
  private var goldCount = 0
  private var silverCount = 0
  private var ecResult:scala.xml.Node = null;

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

  private def storeEcResult(r: scala.xml.Node) = {
    //println("storing Ec result, length = " + r.toString().length());
    ecResult = r;
  }
  
  private def end = {
    if (!discardedCases.isEmpty) {
      logFile.close
    }
    if (nxParserImpl.endsWith("HPAExpcontextNXParser")) {  // ExpContext special case
    	println("Writing final XML to file, length before pretty printing:" + ecResult.toString().length())
    	fw.write(prettyPrinter.format(ecResult) + "\n")        
    } else {
    	fw.write("</object-stream>");
    }
    fw.close;
    listener ! EndActorSystemMSG(success, goldCount, silverCount, discardedCases, files)
  }

  def receive = {
    
  	case StartParsingMSG => {
      if (nxParserImpl.endsWith("HPAExpcontextNXParser")) {  // ExpContext special case
	      files.map(f => workerRouter ! ProcessMSG(nxParserImpl, f))        
      } else {
	      fw.write("<object-stream>\n")
	      files.map(f => workerRouter ! ProcessMSG(nxParserImpl, f))        
      }
    }
    
    case m: SuccessFileParsedMSG => {
      filesCount += 1
      success += 1
      if (nxParserImpl.endsWith("HPAExpcontextNXParser")) {  // ExpContext special case (if any) !
        storeEcResult(m.wrapper.toXML);
      } else {
    	  fw.write(prettyPrinter.format(m.wrapper.toXML) + "\n")        
      }
      m.wrapper.getQuality match {
        case NXQuality.GOLD => goldCount+=1
        case NXQuality.SILVER => silverCount+=1
      }
      checkEnd
    }
    
    
    case m: NXExceptionFoundMSG => {
      filesCount += 1
      discardedCases += m.exception;
      if (m.exception.getNXExceptionType.isError) {
        logFile.write(m.exception.getFile.getAbsolutePath() + "\n");
        println(m.exception.getFile.getName() + " - " + m.exception.getNXExceptionType.getClass().getSimpleName() + " " + m.exception.getMessage)
      }
      checkEnd
    }
    
    case _ => {
      println("Unexpected message received ")
      end
    }
  }

}

