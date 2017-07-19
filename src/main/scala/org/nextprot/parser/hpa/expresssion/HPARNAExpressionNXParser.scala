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
import org.nextprot.parser.hpa.subcell.cases.CASE_ASSAY_TYPE_NOT_TISSUE
import org.nextprot.parser.hpa.expcontext.HPAExpcontextNXParser
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssocProperty
import org.nextprot.parser.core.datamodel.annotation.ExperimentalContextSynonym
import org.nextprot.parser.hpa.datamodel.ExpHPARNAAnnotationsWrapper
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.core.constants.EvidenceCode


class HPARNAExpressionNXParser extends NXParser {

  
  var pInfo : String = null;
  
  def parsingInfo: String = {
    return pInfo;
  }
  

  def parse(fileName: String): TemplateModel = {

    val teSection = "rnaExpression"
    val assayType = "tissue" // used for building the accession (and thus URL) or evidences (AnnotationResourceAssocs)
    val entryElem = scala.xml.XML.loadFile(new File(fileName))
    val ensgId = HPAUtils.getEnsgId(entryElem)
    val accession = HPAUtils.getAccession(entryElem);
    HPAValidation.checkPreconditionsForRnaExpr(accession, entryElem)
    //val summaryDescr = HPAUtils.getTissueExpressionSummary(entryElem)
    val uniprotIds = HPAUtils.getAccessionList(entryElem)
    val rnatedmap = HPAUtils.getTissueRnaExpression(entryElem)

    val quality = GOLD
    val ruleUsed = "as defined for RNA expression in NEXTPROT-1383";

    //Stats should not appear here
    Stats ++ ("RULES_FOR_" + quality, ruleUsed);

    pInfo = quality.toString();  // + "\t" + ruleUsed;
    	
    if (quality.equals(BRONZE)) { // Should never happen after HPA16
            Stats ++ ("BRONZE", "bronze")
            throw new NXException(CASE_BRONZE_QUALITY);
    }
    
    val rnatedlist = rnatedmap.map(rnated =>  { // convert map to TissueExpressionData objects
    new TissueExpressionData(rnated._1, null, rnated._2)}).toList // We may have to look-up celllines if they are same as IHC counterpart

    val rnatsAnnotations = rnatedlist.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) != null).
      map(rnated => {
        val syn = HPAExpcontextUtil.getSynonymForXml(rnated, EvidenceCode.RnaSeq) // The Expcontext synonym allows to link data between expression and expcontext xmls
        extractTissueSpecificityAnnotation(ensgId, NXQuality.GOLD, syn, rnated.level, assayType, EvidenceCode.RnaSeq) // Always GOLD
      }).toList // Creates the list of raw annotations
    //Console.err.println(ensgId + ": " + rnatsAnnotations.size + " RNA annotations...")
      

      new ExpHPARNAAnnotationsWrapper(
      _quality = quality,
      _ensgAc = ensgId,
      _uniprotIds = uniprotIds,
      //_summaryAnnotation = extractSummaryAnnotation(ensgId, quality, summaryDescr, assayType),
      _rowAnnotations = rnatsAnnotations 
      )
  }

  /**
   * Extract tissue expression annotation
   */
  private def extractTissueSpecificityAnnotation(identifier: String, quality: NXQuality, synonym: String, level: String, assayType: String , eco : EvidenceCode.Value): RawAnnotation = {
    return new RawAnnotation(
      _qualifierType = "EXP",
      _datasource = null, // "Human protein atlas",
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
        res.group(1)
      }
      case None => {
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
      _dataSource = "Human protein atlas RNA-seq",
      _props = List(new AnnotationResourceAssocProperty("expressionLevel", level)),
      _expContext = new ExperimentalContextSynonym(synonym))
  }

}