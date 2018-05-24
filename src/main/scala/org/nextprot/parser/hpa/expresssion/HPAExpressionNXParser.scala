package org.nextprot.parser.hpa.expression

import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.datamodel.TemplateModel
import java.io.File
import scala.xml.Node
import scala.xml.NodeSeq
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.expcontext._
import scala.collection.mutable.MutableList
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.subcell.HPAValidation
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.cases.CASE_BRONZE_QUALITY
import org.nextprot.parser.core.datamodel.annotation.AnnotationListWrapper
import org.nextprot.parser.core.datamodel.annotation.RawAnnotation
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssoc
import org.nextprot.parser.hpa.expcontext.HPAExpcontextNXParser
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssocProperty
import org.nextprot.parser.core.datamodel.annotation.ExperimentalContextSynonym
import org.nextprot.parser.hpa.datamodel.ExpHPAAnnotationsWrapper
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.core.constants.EvidenceCode
import org.nextprot.parser.hpa.subcell.cases._


object Caloha {
  val map = HPAExpcontextConfig.readTissueMapFile.map;
}

class HPAExpressionNXParser extends NXParser {

  val parserDatasource = "Human protein atlas"
  val endUserDatasource = "Human protein atlas"
  
  var pInfo : String = null;
  
  def parsingInfo: String = {
    return pInfo;
  }
  

  def parse(fileName: String): TemplateModel = {

    val teSection = "tissueExpression"
    val assayType = "tissue" // used for building the accession (and thus URL) or evidences (AnnotationResourceAssocs)
    val entryElem = scala.xml.XML.loadFile(new File(fileName))
    val uniprotIds = HPAUtils.getAccessionList(entryElem)
    val antibodyIds = HPAUtils.getAntibodyIdListForExpr(entryElem)
    val ensgId = HPAUtils.getEnsgId(entryElem)
    val integrationLevel = HPAUtils.getTissueExpressionType(entryElem)
    val summaryDescr = HPAUtils.getTissueExpressionSummary(entryElem)
    
    HPAValidation.checkPreconditionsForExpr(entryElem)

    val qualityRule = HPAQuality.getQuality(entryElem, teSection);
    val quality = qualityRule._1;
    val ruleUsed = "as defined in NEXTPROT-1383";

    //Stats should not appear here
    Stats ++ ("RULES_FOR_" + quality, ruleUsed);

    pInfo = quality.toString();  // + "\t" + ruleUsed;
    	
    if (quality.equals(BRONZE)) { // Should never happen after HPA16
            Stats ++ ("BRONZE", "bronze")
            throw new NXException(CASE_BRONZE_QUALITY);
    }

    var data = HPAUtils.getTissueExpressionNodeSeq(entryElem) \ "data"
    val teds = data.map(HPAExpcontextUtil.createTissueExpressionLists(_)).flatten;

    teds.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) == null).
      foreach(ted => println("WARNING: no CALOHA mapping found for " + ted.toString + ", file " + fileName))

    val ihctsAnnotations = teds.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) != null).
      map(ted => {
        val syn = HPAExpcontextUtil.getSynonymForXml(ted, EvidenceCode.ImmunoHistochemistry)
        extractTissueSpecificityAnnotation(ensgId, quality, syn, ted.level, assayType, EvidenceCode.ImmunoHistochemistry)
      }).toList

      new ExpHPAAnnotationsWrapper(
      _quality = quality,
      _ensgAc = ensgId,
      _uniprotIds = uniprotIds,
      _antibodyIds = antibodyIds,
      _integrationLevel = integrationLevel,
      _summaryAnnotation = extractSummaryAnnotation(ensgId, quality, summaryDescr, assayType),
      _rowAnnotations = ihctsAnnotations,
      _datasource = parserDatasource,
      _annotationTag = "IHC"
      )
  }

  /**
   * Extract tissue expression annotation
   */
  private def extractTissueSpecificityAnnotation(identifier: String, quality: NXQuality, synonym: String, level: String, assayType: String , eco : EvidenceCode.Value): RawAnnotation = {
    return new RawAnnotation(
      _qualifierType = "EXP",
      _datasource = null, // "Human protein atlas" is set above in WrappedBean
      _cvTermAcc = null,
      _cvTermCategory = null,
      _isPropagableByDefault = false,
      _type = "tissue specificity",
      _description = null,
      _quality = quality,
      _assocs = List(extractTsAnnotationResourceAssoc(identifier, quality, synonym, level, assayType, eco)))
  }

  /**
   * Extract annotation resource assoc for tissue specificity annotation
   */
  private def extractTsAnnotationResourceAssoc(identifier: String, quality: NXQuality, synonym: String, level: String, assayType: String, eco : EvidenceCode.Value): AnnotationResourceAssoc = {
    // we regard the evidence as negative it the protein is not detected 
    val negState: Boolean = (level == "not detected")
    // we need to extract the tissue name from the synonym 
    val pattern = """tissue->([^;]+);""".r
    val tissue = pattern.findFirstMatchIn(synonym) match {
      case Some(res) => {
        //println("synonym:"+synonym)
        //println("result:"+res.group(1))
        res.group(1)
      }
      case None => {
        //println("synonym:"+synonym)
        throw new Exception("Could not find tissue pattern in synonym !")
      }
    }
   
    return new AnnotationResourceAssoc(
      _resourceClass = "source.DbXref",
      _resourceType = "DATABASE",
      _accession = identifier + "/" + assayType + "/" + tissue,
      _cvDatabaseName = "HPA",
      _eco = eco.code,
      _isNegative = negState,
      _type = "EVIDENCE",
      _quality = quality,
      _dataSource = endUserDatasource,
      _props = List(new AnnotationResourceAssocProperty("expressionLevel", level)),
      _expContext = new ExperimentalContextSynonym(synonym))
  }

  private def extractSummaryAnnotation(identifier: String, quality: NXQuality, description: String, assayType: String): RawAnnotation = {
    return new RawAnnotation(
      _qualifierType = "EXP",
      _datasource = null, // "Human protein atlas", set above in WrappedBean
      _isPropagableByDefault = false,
      _quality = quality,
      _cvTermAcc = null,
      _cvTermCategory = null,
      _description = description,
      _type = "expression info",
      _assocs = List(new AnnotationResourceAssoc(
        _resourceClass = "source.DbXref",
        _resourceType = "DATABASE",
        _accession = identifier + "/" + assayType,
        _cvDatabaseName = "HPA",
        _eco = EvidenceCode.ImmunoLocalization.code,
        _isNegative = false,
        _type = "EVIDENCE",
        _quality = quality,
        _dataSource = parserDatasource,
        _props = null, _expContext = null)))
  }

}