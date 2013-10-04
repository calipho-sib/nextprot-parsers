package org.nextprot.parser.core

import java.io.File
import java.io.OutputStream
import org.nextprot.parser.core.datamodel.TemplateModel

/**
 * Parsers must implement this trait
 */
trait NXParser {
  /**
   * Parse an identifier
   * @param identifier can be a file name, url, ...
   */
  def parse(identifier: String): TemplateModel
}
