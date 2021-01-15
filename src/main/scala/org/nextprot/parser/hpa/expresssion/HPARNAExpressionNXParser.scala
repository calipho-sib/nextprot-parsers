package org.nextprot.parser.hpa.expression

import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.datamodel.TemplateModel
import java.io.File
import scala.xml.NodeSeq
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.expcontext._
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.subcell.HPAValidation
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.cases.CASE_BRONZE_QUALITY
import org.nextprot.parser.core.datamodel.annotation.RawAnnotation
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssoc
import org.nextprot.parser.core.datamodel.annotation.AnnotationResourceAssocProperty
import org.nextprot.parser.core.datamodel.annotation.ExperimentalContextSynonym
import org.nextprot.parser.hpa.datamodel.ExpHPARNAAnnotationsWrapper
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.core.constants.EvidenceCode
import org.nextprot.parser.hpa.HPAConfig


object HPAMultiENSGTList {
  val multiENSGTList = HPAConfig.readHPAMultiENSGListFile;
}

class HPARNAExpressionNXParser extends NXParser {

  val endUserDatasource = "Human protein atlas"
  
  var pInfo : String = null;
  
  def parsingInfo: String = {
    return pInfo;
  }
  

  def parse(fileName: String): TemplateModel = {

    val teSection = "rnaExpression"
    val entryElem = scala.xml.XML.loadFile(new File(fileName))
    val ensgId = HPAUtils.getEnsgId(entryElem)
    HPAValidation.checkPreconditionsForRnaExpr(entryElem, HPAMultiENSGTList.multiENSGTList)
    val uniprotIds = HPAUtils.getAccessionList(entryElem)

    // TODO: need some refactoring between all RNA-seq data but no time to do it now
    // Creates the list of RNA-seq consensusTissue raw annotations
    val rnaConsensusTissueMap = HPAUtils.getRnaExpression(entryElem, "consensusTissue", "tissue")
    val rnaConsensusTissueTedList = convertMapTissueExpressionData(rnaConsensusTissueMap);
    val rnaConsensusTissueAnnotations = rnaConsensusTissueTedList.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) != null).
      map(rnated => {
        val syn = HPAExpcontextUtil.getSynonymForXml(rnated, EvidenceCode.RnaSeq) // The Expcontext synonym allows to link data between expression and expcontext xmls
        val accSuffix = "tissue/" + rnated.tissue + "#rnaseq";
        extractTissueSpecificityAnnotation(ensgId, NXQuality.GOLD, syn, rnated.level, accSuffix, EvidenceCode.RnaSeq) // Always GOLD
      }).toList // Creates the list of raw annotations

    // Creates the list of RNA-seq humanBrain raw annotations
    val rnaHumanBrainMap = HPAUtils.getRnaExpression(entryElem, "humanBrain", "tissue")
    val rnaHumanBrainTedList = convertMapTissueExpressionData(rnaHumanBrainMap);
    val rnaHumanBrainAnnotations = rnaHumanBrainTedList.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) != null).
      map(rnated => {
        val syn = HPAExpcontextUtil.getSynonymForXml(rnated, EvidenceCode.RnaSeq) // The Expcontext synonym allows to link data between expression and expcontext xmls
        val accSuffix = "brain/" + rnated.tissue + "#pfc_rnaseq";
        extractTissueSpecificityAnnotation(ensgId, NXQuality.GOLD, syn, rnated.level, accSuffix, EvidenceCode.RnaSeq) // Always GOLD
      }).toList // Creates the list of raw annotations

    // Creates the list of RNA-seq blood raw annotations
    val rnaBloodMap = HPAUtils.getRnaExpression(entryElem, "blood", "bloodCell")
    val cellTypeTolineageMap = getCellTypeTolineageMap(entryElem)
    val rnaBloodTedList = convertMapTissueExpressionData(rnaBloodMap);
    val rnaBloodAnnotations = rnaBloodTedList.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) != null).
      map(rnated => {
        val syn = HPAExpcontextUtil.getSynonymForXml(rnated, EvidenceCode.RnaSeq) // The Expcontext synonym allows to link data between expression and expcontext xmls
        val lineage = cellTypeTolineageMap.get(rnated.tissue) match {
          case Some(cme) => cme
          case None => new Exception("Lineage not found for : " + rnated.tissue)
        }
        val accSuffix = "blood/" + lineage + "#hpa_" + rnated.tissue.toLowerCase().replace(" ", "_");
        extractTissueSpecificityAnnotation(ensgId, NXQuality.GOLD, syn, rnated.level, accSuffix, EvidenceCode.RnaSeq) // Always GOLD
      }).toList

    // Creates the list of scRNA-seq raw annotations
    val scRnaExpressionDataMap = HPAUtils.getScRnaExpression(entryElem)
    val scRnaList = convertMapCellTypeExpressionData(scRnaExpressionDataMap)
    val scRnaAnnotations = scRnaList.filter(HPAExpcontextUtil.getCalohaMapping(_, Caloha.map) != null).
      map(scrnated => {
        val syn = HPAExpcontextUtil.getSynonymForXml(scrnated, EvidenceCode.scRnaSeq) // The Expcontext synonym allows to link data between expression and expcontext xmls
        extractTissueSpecificityAnnotation(ensgId, NXQuality.GOLD, syn, scrnated.level, "celltype", EvidenceCode.scRnaSeq) // Always GOLD
      }).toList

    var hasExpression: Boolean = false
    val expressionMap = rnaConsensusTissueMap ++ rnaHumanBrainMap ++ rnaBloodMap ++ scRnaExpressionDataMap
    expressionMap foreach (x => hasExpression |= (x._2 != "not detected"))
    if (!hasExpression) {
      Console.err.println(ensgId + ": " + expressionMap.size + " expression 'not detected'")
    }

    val quality = GOLD
    val ruleUsed = "as defined for RNA expression in NEXTPROT-1383";

    //Stats should not appear here
    Stats ++ ("RULES_FOR_" + quality, ruleUsed);

    pInfo = quality.toString();

    if (quality.equals(BRONZE)) { // Should never happen after HPA16
      Stats ++ ("BRONZE", "bronze")
      throw new NXException(CASE_BRONZE_QUALITY);
    }

    new ExpHPARNAAnnotationsWrapper(
      _quality = quality,
      _ensgAc = ensgId,
      _uniprotIds = uniprotIds,
      _summaryAnnotations = extractSummaryAnnotations(ensgId, entryElem),
      _rowAnnotations = rnaConsensusTissueAnnotations ::: rnaBloodAnnotations ::: rnaHumanBrainAnnotations ::: scRnaAnnotations
    )
  }

  private def convertMapTissueExpressionData(map: Map[String, String]) = {
    map.map(expData => new TissueExpressionData(expData._1, null, expData._2)).toList
  }

  private def convertMapCellTypeExpressionData(map: Map[String, String]) = {
    map.map(expData => new TissueExpressionData(null, expData._1, expData._2)).toList
  }

  def getCellTypeTolineageMap(entryElem: NodeSeq): Map[String, String] = {
    val map = ((entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "blood") }) \ "data")
      .map(f => ((f \ "bloodCell").text, (f \ "bloodCell" \\ "@lineage").text))
      .toMap
    return map
  }

  /**
   * Extract tissue expression annotation
   */
  private def extractTissueSpecificityAnnotation(identifier: String, quality: NXQuality, synonym: String, level: String,
                                                 accSuffix: String , eco : EvidenceCode.Value): RawAnnotation = {
    return new RawAnnotation(
      _qualifierType = "EXP",
      _datasource = null, // "Human protein atlas RNA-seq" is set above
      _cvTermAcc = null,
      _cvTermCategory = null,
      _isPropagableByDefault = false,
      _type = "tissue specificity",
      _description = null,
      _quality = quality,
      _assocs = List(extractTsAnnotationResourceAssoc(identifier, quality, synonym, level, accSuffix, eco)))
  }

  /**
   * Extract annotation resource assoc for tissue specificity annotation
   */
  private def extractTsAnnotationResourceAssoc(identifier: String, quality: NXQuality, synonym: String, level: String,
                                               accSuffix: String, eco : EvidenceCode.Value): AnnotationResourceAssoc = {
    // we regard the evidence as negative it the protein is not detected 
    val negState: Boolean = (level == "not detected")

    return new AnnotationResourceAssoc(
      _resourceClass = "source.DbXref",
      _resourceType = "DATABASE",
      _accession = identifier + "/" + accSuffix,
      _cvDatabaseName = "HPA",
      _eco = eco.code,
      _isNegative = negState,
      _type = "EVIDENCE",
      _quality = quality,
      _dataSource = endUserDatasource,
      _props = List(new AnnotationResourceAssocProperty("expressionLevel", level)),
      _expContext = new ExperimentalContextSynonym(synonym))
  }

  private def extractSummaryAnnotations(identifier: String, entryElem: NodeSeq): List[RawAnnotation] = {
    List(extractRNASpecificityAndDistributionAnnotations(identifier, entryElem),
      extractScRNASpecificityAnnotation(identifier, entryElem))
  }

  private def extractRNASpecificityAndDistributionAnnotations(identifier: String, entryElem: NodeSeq): RawAnnotation = {
    val elmt = entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "consensusTissue") }

    var specificity: String = extractSpecificity(elmt, "rnaSpecificity",
      "tissue", "specificity", "RNA tissue specificity")

    val distributionElmt = elmt.map(f => f \ "rnaDistribution")
    var distribution = "";
    if (distributionElmt.nonEmpty) {
      distribution = "RNA tissue distribution: " + distributionElmt.head.text + ".";
    }

    val summary = specificity + (if (specificity.nonEmpty) " " else "") + distribution;

    return new RawAnnotation(
      _datasource = null,
      _cvTermAcc = null,
      _cvTermCategory = null,
      _qualifierType = "EXP",
      _isPropagableByDefault = false,
      _type = "expression info",
      _description = summary,
      _quality = null,
      _assocs = List(new AnnotationResourceAssoc(
        _resourceClass = "source.DbXref",
        _resourceType = "DATABASE",
        _accession = identifier + "/tissue",
        _cvDatabaseName = "HPA",
        _eco = EvidenceCode.RnaSeq.code,
        _isNegative = false,
        _type = "SOURCE",
        _quality = null,
        _dataSource = null,
        _props = null,
        _expContext = null)))
  }

  private def extractScRNASpecificityAnnotation(identifier: String, entryElem: NodeSeq): RawAnnotation = {
    val elmt = entryElem \ "cellTypeExpression"

    val specificity: String = extractSpecificity(elmt, "cellTypeSpecificity",
      "cellType", "category", "Single cell type specificity")

    return new RawAnnotation(
      _datasource = null,
      _cvTermAcc = null,
      _cvTermCategory = null,
      _qualifierType = "EXP",
      _isPropagableByDefault = false,
      _type = "expression info",
      _description = specificity,
      _quality = null,
      _assocs = List(new AnnotationResourceAssoc(
        _resourceClass = "source.DbXref",
        _resourceType = "DATABASE",
        _accession = identifier + "/celltype",
        _cvDatabaseName = "HPA",
        _eco = EvidenceCode.scRnaSeq.code,
        _isNegative = false,
        _type = "SOURCE",
        _quality = null,
        _dataSource = null,
        _props = null,
        _expContext = null)))
  }

  private def extractSpecificity(elmt: NodeSeq, subsetName: String, tagName: String, attrName:String,
                                prefix: String) = {

    val specificityElmt = elmt.map(f => f \ subsetName)
    var specificity = "";
    if (specificityElmt.nonEmpty) {
      val t = (specificityElmt.head \ tagName).map(f => f.text)

      // Note: there is an issue in HPA files. To avoid to display it, we do this replacement
      var specificTissues = t.mkString(", ").replace("Alveolar cells type, Alveolar cells type",
                                    "Alveolar cells type 1, Alveolar cells type 2")
      if (specificTissues.nonEmpty)
        specificTissues = " (" + specificTissues + ")"
      specificity = prefix + ": " + (specificityElmt.head \\ ("@" + attrName)).text + specificTissues + ".";
    }
    specificity
  }
}