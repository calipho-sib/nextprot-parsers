package org.nextprot.parser.core
import java.io.File
import java.io.OutputStream
import scala.io.Source
import scala.Array.canBuildFrom
import java.io.FileWriter
import scala.collection.mutable.ArrayBuffer
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import org.nextprot.parser.core.actor.message.StartParsingMSG
import org.nextprot.parser.core.actor.NXListener
import org.nextprot.parser.core.actor.NXMaster
import NXProperties._

/**
 * Main application for parsing a proxy directory and create a single output file <br>
 * @author Daniel Teixeira
 */
object NXParserApp extends NXParserAppBase {
  
  initialize

}