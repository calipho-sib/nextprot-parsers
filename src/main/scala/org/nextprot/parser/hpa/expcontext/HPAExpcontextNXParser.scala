package org.nextprot.parser.hpa.expcontext

import java.io.File

import org.nextprot.parser.core.NXParser
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.HPAUtils.dataToNotExclude

import scala.xml.NodeSeq

class HPAExpcontextNXParser extends NXParser {
  
	def parsingInfo(): String = return null;

    def parse(fileName: String): TissueExpressionDataSet = {

	    //println("start parsing " + fileName);
	    val entryElem = scala.xml.XML.loadFile(new File(fileName));
	    val ensgId = HPAUtils.getEnsgId(entryElem)
	
	    // IHC data
	    val tissueExpression = entryElem \ "tissueExpression";
	    val dataset =  (tissueExpression \ "data").flatMap(HPAExpcontextUtil.createTissueExpressionLists(ensgId, _, "tissue")).toSet;

	    // RNA-seq data
	    val rnaConsensusTissueExprMap = ((entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "consensusTissue") }))
	    val rnaBrainExprMap = ((entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "humanBrain") }))
	    val rnaBloodExprMap = ((entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "blood") }))
	    val consensusTissueDatasetRna = getDatasetRna(ensgId, rnaConsensusTissueExprMap, "tissue");
	    val brainDatasetRna = getDatasetRna(ensgId, rnaBrainExprMap, "tissue");
	    val bloodDatasetRna = getDatasetRna(ensgId, rnaBloodExprMap, "bloodCell");

	    // scRNA-seq data
	    val datasetSingleCellRna = getDatasetScRna(entryElem \ "cellTypeExpression")

	    val alldata = dataset ++ datasetSingleCellRna ++ consensusTissueDatasetRna ++ bloodDatasetRna ++ brainDatasetRna

	    new TissueExpressionDataSet(alldata);
  }

	private def getDatasetRna(ensgId: String, rnaExprMap: NodeSeq, tagName: String) = {
		(rnaExprMap \ "data" filter dataToNotExclude(tagName))
	    .filter(el => (el \ tagName).text != "").flatMap(HPAExpcontextUtil.createTissueExpressionLists(ensgId, _, tagName)).toSet
	}
	private def getDatasetScRna(scRnaExprMap: NodeSeq) = {
		(scRnaExprMap \ "singleCellTypeExpression")
	    .map(el => new TissueExpressionData(null, (el \ "@name").text, HPAUtils.getCheckedRNALevel(el))).toSet
	}
}