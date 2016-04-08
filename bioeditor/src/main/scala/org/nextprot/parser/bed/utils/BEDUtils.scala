package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq

object BEDUtils {

  def getBedAnnotations(bedEntry: NodeSeq): String = {
    val entryName = (bedEntry \ "nxprotein" \ "@accession").text;
    return entryName;
  }

}