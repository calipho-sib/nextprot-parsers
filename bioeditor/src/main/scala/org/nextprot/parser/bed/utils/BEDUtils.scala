package org.nextprot.parser.bed.utils

import scala.xml.NodeSeq
import org.nextprot.parser.bed.datamodel.BEDVariant
import org.nextprot.parser.bed.datamodel.BEDAnnotation
import org.nextprot.parser.bed.datamodel.BEDAnnotation

object BEDUtils {

  def getEntryAccession(entry: NodeSeq): String = {
    return (entry \ "nxprotein" \ "@accession").text;
  }

  def getBEDVariants(entry: NodeSeq): List[BEDVariant] = {
    return (entry \\ "variant").map(xmlV => new BEDVariant((xmlV \ "@uniqueName").text)).toList;
  }
  
  def getBEDAnnotations(entry: NodeSeq): List[BEDAnnotation] = {
    return (entry \\ "annotations" \\ "annotation").map(xmlA => {

      val _subject = (xmlA \ "subject" \ "molecularEntityRef").text
      val _relation = (xmlA \ "relationship" \ "cvName").text
      val _object = (xmlA \ "object" \ "term" \ "cvName").text
      
      new BEDAnnotation(_subject, _relation, _object);
      
    }).toList;
  }
}