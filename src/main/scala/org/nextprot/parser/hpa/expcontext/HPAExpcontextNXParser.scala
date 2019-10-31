package org.nextprot.parser.hpa.expcontext

import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.datamodel.TemplateModel
import java.io.File

import scala.xml.Node
import scala.xml.NodeSeq
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.HPAUtils.dataNotBloodOrgan

class HPAExpcontextNXParser extends NXParser {
  
	def parsingInfo(): String = return null;

    def parse(fileName: String): TissueExpressionDataSet = {

	    //println("start parsing " + fileName);
	    val entryElem = scala.xml.XML.loadFile(new File(fileName));
	
	    val tissueExpression = entryElem \ "tissueExpression";
	    val rnaExpression = entryElem \ "rnaExpression";
	    val rnaConsensusTissueExprMap = ((entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "consensusTissue") }))
	    val rnaBloodExprMap = ((entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "blood") }))

	    val assayType = (tissueExpression \ "@assayType").text;
	    val technology = (tissueExpression \ "@technology").text;
	 	    
	    // We have a common expcontext xml for tissueExpression and rnaExpression
	    val dataset =  (tissueExpression \ "data").flatMap(HPAExpcontextUtil.createTissueExpressionLists(_, "tissue")).toSet;
	    // We exclude celllines data from the rnaExpression
	    val consensusTissueDatasetRna = getDatasetRna(rnaConsensusTissueExprMap, "tissue");
	    val bloodDatasetRna = getDatasetRna(rnaBloodExprMap, "bloodCell");
	    val alldata = dataset ++ consensusTissueDatasetRna ++ bloodDatasetRna
	    new TissueExpressionDataSet(alldata);
  }

	private def getDatasetRna(rnaConsensusTissueExprMap: NodeSeq, tagName: String) = {
		(rnaConsensusTissueExprMap \ "data" filter dataNotBloodOrgan(tagName))
			.filter(el => (el \ tagName).text != "").flatMap(HPAExpcontextUtil.createTissueExpressionLists(_, tagName)).toSet
	}
}