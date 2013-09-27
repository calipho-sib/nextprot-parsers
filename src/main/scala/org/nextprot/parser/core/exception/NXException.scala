package org.nextprot.parser.core.exception

import java.io.File

/**
 * Case that is recurrent in the domain where it applies but because of a given rule should be discarded
 */
abstract class DiscardCase(description : String) extends NXExceptionType(false, description);

/**
 * Error case that should be discarded and logged in the error file
 */
abstract class ErrorCase(description : String) extends NXExceptionType(true, description);


/**
 * Unexpected error, always log
 */
object UNEXPECTED_EXCEPTION extends ErrorCase("Unexpected error");


/**
 * Abstract class for exceptions 
 * @param isError defines wheather the case is an error or not (if it is an error, this one will be logged)
 * @param description A description of the error
 * 
 */
sealed abstract class NXExceptionType(val isError: Boolean, val description: String)

/**
 * Expected exceptions returned from the parsing
 * @author Daniel Teixeira
 */
class NXException(nxExceptionType: NXExceptionType, message: String) extends Exception {

  /**
   * Constructor with an empty message, (usually the type and the description of the message is simply enough)
   */
  def this(nxExceptionType: NXExceptionType) = this(nxExceptionType, "")

  /**
   * Gets the message 
   */
  override def getMessage(): String = {
    return message;
  }

  /**
   * The name of the exception
   */
  def getNXExceptionType(): NXExceptionType = {
    return nxExceptionType;
  }

}
