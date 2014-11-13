package org.nextprot.parser.core.datamodel

import scala.xml.Node
import org.nextprot.parser.core.constants.NXQuality.NXQuality

trait TemplateModel {
  def toXML : Node
  def getQuality : NXQuality
}

