package org.nextprot.parser.core

/**
 * Parsers must implement this trait
 */
trait NXParser{
  
  /**
   * Parse an identifier
   * @param identifier can be a file name, url, ...
   */
  def parse(identifier: String): Any  
  
  def parsingInfo: String
  
}
