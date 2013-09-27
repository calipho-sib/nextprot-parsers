package org.nextprot.parser.core

import java.io.File
import java.io.OutputStream
import org.nextprot.parser.core.datamodel.AnnotationListWrapper

/**
 * Parsers must implement this trait
 */
trait NXParser {
  def parseFile(file: File): AnnotationListWrapper
}
