package org.nextprot.parser.core

import java.io.File
import java.io.OutputStream
import org.nextprot.parser.core.datamodel.TemplateModel

/**
 * Reduces call from the Mapper
 */
trait NXReducer {

  def reduce(objects: Any)
  
  def end

  def start
}
