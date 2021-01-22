package org.nextprot.parser.hpa.antibody

import java.io.File
import scala.xml.NodeSeq
import scala.collection.mutable.Map
import scala.collection.mutable.MutableList
import org.nextprot.parser.hpa.subcell.cases._
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
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.HPAValidation

import org.nextprot.parser.hpa.HPAUtils

/**
 * Implementation class for HPA files
 */
class HPAAntibodyNXParser extends NXParser {
    
  /**
   * Parse the file and produces the wrapper containing the list of antibodies
   */

  def parse(fileName: String): AntibodyEntryWrapperList = {

    val entryElem = scala.xml.XML.loadFile(new File(fileName))
    val antibodyElems = (entryElem \ "antibody").toList
    val ensgId = HPAUtils.getEnsgId(entryElem)
    val uniprotIds = HPAUtils.getAccessionList(entryElem)
    HPAValidation.checkPreconditionsForAb(entryElem)  // No specific preconditions for Ab
    val wrappers  = 
      antibodyElems.map(antibodyElem => {
        val proplist : MutableList[AntibodyIdentifierProperty] = MutableList()
        var propvalue : String = ""
          
        val dbxref = (antibodyElem \ "@id").text
        val version = (antibodyElem \ "@releaseVersion").text
        val bioSequence = new BioSequence((antibodyElem \ "antigenSequence").text, "PREST")

        propvalue = (HPAUtils.getTissueExpressionNodeSeq(entryElem) \ "verification").text // The 'global' validation for IH (at entryElem level)
        //Console.err.println("verif elt: " + propvalue)
        if(propvalue != "") {proplist += new AntibodyIdentifierProperty("immunohistochemistry validation",propvalue)}
        // Actually it is not a AntibodyIdentifierProperty since it can have the same value for several antibodies !!
        
        propvalue = (antibodyElem \ "westernBlot" \ "verification").text
        if(propvalue != "") {proplist +=  new AntibodyIdentifierProperty("western blot validation",propvalue)}
        
        propvalue = (antibodyElem \ "proteinArray" \ "verification").text
        if(propvalue != "") {proplist +=  new AntibodyIdentifierProperty("protein array validation",propvalue)}

        val hasIHCExprData = (HPAValidation.checkMainTissueExpression(entryElem) == null)  // null = no error = IHC expr data available
        
        val quality = GOLD; // always GOLD quality for all antibodies regardless of expression data according to 'Specifications HPA'

        val annots = if (hasIHCExprData) {
          val annotations = ((antibodyElem \ "tissueExpression").map(extractAntibodyAnnotation(dbxref, _))).toList;
          new HPAAntibodyAnnotationListWrapper(_HPAaccession = dbxref, _rowAnnotations = annotations)
        } else {
          null
        }

        new AntibodyEntryWrapper(quality.toString(), dbxref, version, new BioSequenceList(List(bioSequence)),
          new AntibodyIdentifierPropertyList(proplist.toList), annots, uniprotIds, ensgId)
      })
      
    new AntibodyEntryWrapperList(wrappers)
  }

  	def parsingInfo(): String = return null;

  
  /**
   * Extract antibody annotation
   */
  private def extractAntibodyAnnotation(identifier: String, locationElem: NodeSeq): RawAnnotation = {
    // qualifier type ?
    // We put the type (normal tissue /cancer) as a header for the elt _description
    val summary = (locationElem \ "summary" \ "@type").text + ":" + (locationElem \ "summary").text
   
    return new RawAnnotation(
      _datasource = null,
      _cvTermAcc = null,
      _cvTermCategory = null,
      _qualifierType = "EXP",
      _isPropagableByDefault = false,
      _type = "expression info",
      _description = summary,
      _quality = null,
      _assocs = List(createAnnotationResourceAssoc(identifier)))
  }

  /**
   * Generates annotation resource assoc
   */
  private def createAnnotationResourceAssoc(identifier: String): AnnotationResourceAssoc = {

    return new AnnotationResourceAssoc(
      _resourceClass = "source.DbXref",
      _resourceType = "DATABASE",
      _accession = identifier,
      _cvDatabaseName = "HPA",
      _eco = EvidenceCode.Antibodymapping.code,
      _isNegative = false,
      _type = "SOURCE",
      _quality = null,
      _dataSource = null,
      _props = null,
      _expContext = null)
  }
  
}
  
