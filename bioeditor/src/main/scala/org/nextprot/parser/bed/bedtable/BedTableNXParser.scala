package org.nextprot.parser.bed.bedtable

import java.io.File
import scala.xml.NodeSeq
import scala.collection.mutable.Map
import scala.collection.mutable.MutableList
import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.datamodel.antibody.AntibodyEntryWrapper
import org.nextprot.parser.core.datamodel.antibody.AntibodyEntryWrapperList
import org.nextprot.parser.core.datamodel.antibody.HPAAntibodyAnnotationListWrapper
import org.nextprot.parser.core.datamodel.biosequence._
import org.nextprot.parser.core.datamodel.antibody.AntibodyIdentifierProperty
import org.nextprot.parser.core.datamodel.antibody.AntibodyIdentifierPropertyList
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssoc
import org.nextprot.parser.core.datamodel.annotation.RawAnnotation
import org.nextprot.parser.core.constants.NXQuality.NXQuality
import org.nextprot.parser.core.constants.EvidenceCode
import org.nextprot.parser.core.datamodel.annotation.AnnotationListWrapper
import org.nextprot.parser.bed.utils.BEDUtils

/**
 * Implementation class for HPA files
 */
class BedTableNXParser extends NXParser {
    
  /**
   * Parse the file and produces the wrapper containing the list of antibodies
   */

  def parse(fileName: String): Void = {

    val entryElem = scala.xml.XML.loadFile(new File(fileName))
      
    val entryName = BEDUtils.getEntryAccession(entryElem);
    
    println("Entry name" + entryName);
   
    return null;
    
  }

  	def parsingInfo(): String = return null;

}
  
