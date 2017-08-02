package org.nextprot.parser.hpa.expcontext

import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.datamodel.TemplateModel
import java.io.File
import scala.xml.Node
import scala.xml.NodeSeq
import org.nextprot.parser.core.exception.NXException

class HPAExpcontextNXParser extends NXParser {
  
	def parsingInfo(): String = return null;

    def parse(fileName: String): TissueExpressionDataSet = {

	    //println("start parsing " + fileName);
	    val entryElem = scala.xml.XML.loadFile(new File(fileName));
	
	    val tissueExpression = entryElem \ "tissueExpression";
	    val rnaExpression = entryElem \ "rnaExpression";
	    val assayType = (tissueExpression \ "@assayType").text;
	    val technology = (tissueExpression \ "@technology").text;
	 	    
	    // We have a common expcontext xml for tissueExpression and rnaExpression
	    val dataset =  (tissueExpression \ "data").map(HPAExpcontextUtil.createTissueExpressionLists(_)).flatten.toSet;
	    // We exclude celllines data from the rnaExpression
	    val datasetrna =  (rnaExpression \ "data").filter(el => (el \ "tissue").text != "").map(HPAExpcontextUtil.createTissueExpressionLists(_)).flatten.toSet;
	    val alldata = dataset ++ datasetrna
	    new TissueExpressionDataSet(alldata);
	    
  }

}