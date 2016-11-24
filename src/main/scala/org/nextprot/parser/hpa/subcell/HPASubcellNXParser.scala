package org.nextprot.parser.hpa.subcell

import java.io.OutputStream
import org.nextprot.parser.core.NXParser
import java.io.File
import scala.xml.NodeSeq
import scala.collection.mutable.Map
import org.nextprot.parser.core.datamodel._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.exception.NXExceptionType
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssoc
import org.nextprot.parser.core.datamodel.annotation.RawAnnotation
import org.nextprot.parser.core.datamodel.annotation.AnnotationListWrapper
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.HPAConfig
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.core.actor.NXWorker
import org.nextprot.parser.hpa.datamodel.SubcellularHPAAnnotationsWrapper
import org.nextprot.parser.core.constants.EvidenceCode
import org.nextprot.parser.core.stats.Stats

object HPASubcellCvTerms {
  val map = HPAConfig.readHPACVTermsMapFile;
}

/**
 * Implementation class for HPA files
 */
class HPASubcellNXParser extends NXParser {

  var pInfo = "";
  
  def parsingInfo: String = {
    return pInfo;
  }

  /**
   * Parse the file and produces the wrapper containing the list of annotations
   */
  def parse(fileName: String): TemplateModel = {

    val entryElem = scala.xml.XML.loadFile(new File(fileName))
    val accession = HPAUtils.getAccession(entryElem);
    
    //println(entryElem)

    HPAValidation.checkPreconditions(accession, entryElem)

    val uniprotIds = HPAUtils.getAccessionList(entryElem)
    val antibodyIds = HPAUtils.getAntibodyIdListForSubcellular(entryElem)
    val ensgId = HPAUtils.getEnsgId(entryElem)
    val integrationLevel = HPAUtils.getSubcellIntegrationType(entryElem)

    val qualityRule = HPAQuality.getQuality(entryElem, "subcellularLocation");
    val quality = qualityRule._1;
    val ruleUsed = qualityRule._2;

    pInfo = quality.toString(); // + "\t" + ruleUsed;
    
	    //Stats should not appear here
	Stats ++ ("RULES_FOR_" + quality, ruleUsed);


    if (quality.equals(BRONZE))
      throw new NXException(CASE_BRONZE_QUALITY);

    val identifier = (entryElem \ "identifier" \ "@id").text;
    val annotations = ((entryElem \ "subcellularLocation" \ "data" \ "location").
        map(extractSubcellularLocationAnnotation(identifier, quality, _))).filter(_!=null).toList;
    if (annotations.isEmpty) throw new NXException(CASE_SUBCELULLAR_MAPPING_NOT_APPLICABLE)
    new SubcellularHPAAnnotationsWrapper(
      _quality = quality,
      _ensgAc = ensgId,
      _uniprotIds = uniprotIds,
      _antibodyIds = antibodyIds,
      _integrationLevel = integrationLevel,
      _rowAnnotations = annotations)
  }

  /**
   * Extract subcellular location annotation
   */
  private def extractSubcellularLocationAnnotation(identifier: String, quality: NXQuality, locationElem: NodeSeq): RawAnnotation = {

    if (!HPASubcellCvTerms.map.contains(locationElem.text))
      throw new NXException(CASE_SUBCELULLAR_MAPPING_NOT_FOUND, locationElem.text)

    val location = HPASubcellCvTerms.map(locationElem.text);
    if (location._1.equals("-")) // Known cases of skipped mapping, eg: aggresome
      return null
    val status = (locationElem \ "@status").text;

    val cvterm = HPASubcellCvTerms.map(locationElem.text)._1;
    val note = HPASubcellCvTerms.map(locationElem.text)._2;

    return new RawAnnotation(
      _qualifierType = null,
      _datasource = "Human protein atlas subcellular localization",
      _isPropagableByDefault = true,
      _quality = quality,
      _cvTermAcc = cvterm,
      _cvTermCategory = "Uniprot subcellular location",
      _description = (if (note != null) { note + ". " } else { "" }) + status.capitalize + " location.",
      _type = "subcellular location",
      _assocs = List(extractAnnotationResourceAssoc(identifier, quality)))
  }

  /**
   * Extract annotation resource assoc
   */
  private def extractAnnotationResourceAssoc(identifier: String, quality: NXQuality): AnnotationResourceAssoc = {

    return new AnnotationResourceAssoc(
      _resourceClass = "source.DbXref",
      _resourceType = "DATABASE",
      _accession = identifier + "/subcellular",
      _cvDatabaseName = "HPA",
      _eco = EvidenceCode.ImmunocytoChemistry.code,
      _isNegative = false,
      _type = "EVIDENCE",
      _quality = quality,
      _dataSource = "Human protein atlas subcellular localization",
      _props = null, _expContext = null)
  }
}
