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
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.cases.CASE_BRONZE_QUALITY
import org.nextprot.parser.core.datamodel.annotation.AnnotationListWrapper
import org.nextprot.parser.core.datamodel.annotation.RawAnnotation
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssoc
import org.nextprot.parser.hpa.subcell.cases.CASE_ASSAY_TYPE_NOT_TISSUE
import org.nextprot.parser.hpa.expcontext.HPAExpcontextNXParser
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssocProperty
import org.nextprot.parser.core.datamodel.annotation.ExperimentalContextSynonym
import org.nextprot.parser.hpa.datamodel.ExpHPAAnnotationsWrapper
import org.nextprot.parser.core.stats.Stats

object Caloha {
  val map = HPAExpcontextConfig.readTissueMapFile.map;
}

class HPAExpressionNXParser extends NXParser {

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

    val quality = HPAQuality.getQuality(entryElem, teSection);
    if (quality.equals(BRONZE)) {
            Stats ++ ("BRONZE", "bronze")
            throw new NXException(CASE_BRONZE_QUALITY);
    }

    val data = HPAUtils.getTissueExpressionNodeSeq(entryElem) \ "data"
    val teds = data.map(HPAExpcontextUtil.createTissueExpressionLists(_)).flatten;

    teds.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) == null).
      foreach(ted => println("WARNING: no CALOHA mapping found for " + ted.toString + ", file " + fileName))

    val tsAnnotations = teds.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) != null).
      map(ted => {
        val syn = HPAExpcontextUtil.getSynonymForXml(ted)
        extractTissueSpecificityAnnotation(ensgId, quality, syn, ted.level, assayType)
      }).toList

    //	    val tedsCount = teds.size
    //	    val tsanCount = tsAnnotations.size
    //	    println("-------------------------------------")
    //	    tsAnnotations.foreach(a => println(a.toXML.toString));
    //	    println("-------------------------------------")
    //	    println("tedsCount:" + tedsCount)
    //	    println("tsanCount:" + tsanCount)

//    new ExpressionHPAAnnotationsWrapper(
//      _quality = quality,
//      _ensgAc = ensgId,
//      _uniprotIds = uniprotIds,
//      _antibodyIds = antibodyIds,
//      _integrationLevel = integrationLevel,
//      _summaryAnnotation = extractSummaryAnnotation(ensgId, quality, summaryDescr, assayType),
//      _exprAnnotations = tsAnnotations)

      new ExpHPAAnnotationsWrapper(
      _quality = quality,
      _ensgAc = ensgId,
      _uniprotIds = uniprotIds,
      _antibodyIds = antibodyIds,
      _integrationLevel = integrationLevel,
      _summaryAnnotation = extractSummaryAnnotation(ensgId, quality, summaryDescr, assayType),
      _rowAnnotations = tsAnnotations)

  
  }

  /**
   * Extract tissue expression annotation
   */
  private def extractTissueSpecificityAnnotation(identifier: String, quality: NXQuality, synonym: String, level: String, assayType: String): RawAnnotation = {
    return new RawAnnotation(
      _qualifierType = "EXP",
      _datasource = null, // "Human protein atlas",
      _cvTermAcc = null,
      _cvTermCategory = null,
      _isPropagableByDefault = false,
      _type = "tissue specificity",
      _description = null,
      _quality = quality,
      _assocs = List(extractTsAnnotationResourceAssoc(identifier, quality, synonym, level, assayType)))
  }

  /**
   * Extract annotation resource assoc for tissue specificity annotation
   */
  private def extractTsAnnotationResourceAssoc(identifier: String, quality: NXQuality, synonym: String, level: String, assayType: String): AnnotationResourceAssoc = {
    // we regard the evidence as negative it the protein is not detected 
    val negState: Boolean = (level == "not detected")
    // we need to extract the tissue name from the synonym 
    //val pattern = """tissue->([\w\s]+);""".r
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
      _ecoCode = "ECO:0000087",
      _ecoName = "Immunolocalization evidence",
      _isNegative = negState,
      _type = "EVIDENCE",
      _quality = quality,
      _dataSource = "Human protein atlas",
      _props = List(new AnnotationResourceAssocProperty("expressionLevel", level)),
      _expContext = new ExperimentalContextSynonym(synonym))
  }

  private def extractSummaryAnnotation(identifier: String, quality: NXQuality, description: String, assayType: String): RawAnnotation = {
    return new RawAnnotation(
      _qualifierType = "EXP",
      _datasource = "Human protein atlas",
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
        _ecoCode = "ECO:0000087",
        _ecoName = "Immunolocalization evidence",
        _isNegative = false,
        _type = "EVIDENCE",
        _quality = quality,
        _dataSource = "Human protein atlas",
        _props = null, _expContext = null)))
  }

}