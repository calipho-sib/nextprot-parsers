package org.nextprot.parser.core.actor.message

import java.io.File
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.datamodel.TemplateModel

sealed trait NXMessage

/**
 * Invokes the beginning of the parsing
 */
object StartParsingMSG extends NXMessage


/**
 * Message sent from a [[org.nextprot.parser.core.actor.NXMaster]] to a [[org.nextprot.parser.core.actor.NXWorker]] to parse a file
 * @param parserImpl the name of the parser implementation class
 * @param file the file to parse
 */
case class ProcessMSG(val parserImpl: String, val file: File) extends NXMessage

/**
 * Message sent from the  [[org.nextprot.parser.core.actor.NXWorker]] to the  [[org.nextprot.parser.core.actor.NXMaster]] to communicate that the file was parsed correctly
 * @param wrapper the wrapper (that contains an xml representation that resulted from the parsing
 */
case class SuccessFileParsedMSG(val wrapper: TemplateModel) extends NXMessage

/**
 * Message sent from the  [[org.nextprot.parser.core.actor.NXWorker]] to the  [[org.nextprot.parser.core.actor.NXMaster]] to communicate that the file failed to be parsed because of a known reason
 * @param exception the nextprot exception 
 */
case class NXExceptionFoundMSG(val exception: NXException) extends NXMessage

/**
 * Message sent from the  [[org.nextprot.parser.core.actor.NXMaster]] to the  [[org.nextprot.parser.core.actor.NXListener]] to communicate that all workers have finished 
 * @param success number of successful file parser
 * @param nxerrors the expected errors
 * @param errors the unexpected errors
 * @param files the files
 */
case class EndActorSystemMSG(val success: Int, val goldCount : Int, val silverCount : Int,  val errors : Traversable[NXException], files : List[File] ) extends NXMessage
