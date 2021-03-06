package org.nextprot.parser.core.actor
import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import java.io.File
import java.io.OutputStream
import scala.Array.canBuildFrom
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer
import java.util.Collection
import java.io.File
import org.nextprot.parser.core.actor.message.EndActorSystemMSG
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.NXProperties
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.core.actor.message.NXProcessedFileMSG

/**
 * Actor responsible responsible to print a final report and shutdown the actor system.
 * @author Daniel Teixeira
 */

class NXListener extends Actor {

  private val detailedInfoMsgs = ArrayBuffer[NXProcessedFileMSG]();

  val time = System.nanoTime();

  def receive = {
    case m: EndActorSystemMSG => {
      Stats.printStats;
      writeDetails;
      
      context.system.terminate()
    }
    case m: NXProcessedFileMSG => {
      detailedInfoMsgs.append(m);
    }
    case m: Any => {
      println("Unexpected message received " + m)
    }
  }

  /*
  def printStats(success: Int, goldCount: Int, silverCount: Int, errors: Traversable[NXException], files: List[File]) = {
    var existError = false;
    println
    println("############################################## Parsing statistics #########################################################")
    println("From a total of " + files.size + " entries: " + success + " entries were successfully parsed and " + errors.size + " discarded.");
    println("Number of GOLD entries: " + goldCount);
    println("Number of SILVER entries: " + silverCount);
    println("----------------------------------------------------------------------------------------------------------------------------")
    println("Discard / Error cases: ")
    errors.groupBy(e => e.getNXExceptionType).toList.sortWith(_._2.size > _._2.size).map(
      f => {
        if (f._1.isError) {
          existError = true;
          println("\tERROR - Found " + f._2.size + " errors (" + f._1.getClass().getSimpleName() + "): " + f._1.description)
        } else {
          println("\tINFO - Found " + f._2.size + " discarded cases (" + f._1.getClass().getSimpleName() + "): " + f._1.description)
        }
      })
    println("----------------------------------------------------------------------------------------------------------------------------")
    println("Finished in " + (System.nanoTime - time) / 1e9 + " seconds")
    println("----------------------------------------------------------------------------------------------------------------------------")
    println("Ouput file: " + System.getProperty(NXProperties.outputFileProperty))
    if (existError) {
      println("Some errors occured, quelle misere...: " + System.getProperty(NXProperties.failedFileProperty));

    } else {
      println("Parsing fully successful. Bravo!")
    }
  }*/

  def writeDetails() = {

    val detailsFileName = "details.tsv";
    val fw = new FileWriter(detailsFileName, false)
    
    println("Writing details in " + detailsFileName + ". Remember to put this file in stats folder with its correct name.");

    detailedInfoMsgs.sortBy(m => m.file.getName()).foreach(
      m => {
        fw.write(m.file.getName() + "\t" + m.info + "\n")
      });
    
    fw.close();
  }

  /*
  def printErrorDetails(errors: Traversable[NXException]) = {

    val detailsFileName = "hpa-subcell-error-details.log";
    val fw = new FileWriter(detailsFileName, false)

    errors.groupBy(e => e.getNXExceptionType).toList.map(
      exs => {
        fw.write(exs._2.size + " cases with " + exs._1.getClass().getSimpleName() + " (" + exs._1.description + "):\n")
        exs._2.foreach(id => {
          fw.write(id.getFile.getName() + "\n");
        })
      })
    println("Detailed file: " + detailsFileName)
    //println("Errors found:" + errors.size)

  }*/

}

