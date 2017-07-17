package org.nextprot.parser.hpa.expcontext

import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.datamodel.TemplateModel
import java.io.File
import scala.xml.Node
import scala.xml.NodeSeq
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases.CASE_ASSAY_TYPE_NOT_TISSUE

class HPAExpcontextNXParser extends NXParser {
  
	def parsingInfo(): String = return null;

    def parse(fileName: String): TissueExpressionDataSet = {

	    //println("start parsing " + fileName);
	    val entryElem = scala.xml.XML.loadFile(new File(fileName));
	
	    val tissueExpression = entryElem \ "tissueExpression";
	    val rnaExpression = entryElem \ "rnaExpression";
	    val assayType = (tissueExpression \ "@assayType").text;
	    val technology = (tissueExpression \ "@technology").text;
	 
	    // we discard files where assay type is not tissue (not anymore now that RNA expression is also combined in expcontext)
	    /*if (! assayType.equalsIgnoreCase("tissue")) {
	    	val msg = "Ignoring assayType " + assayType 
	    	throw new NXException(CASE_ASSAY_TYPE_NOT_TISSUE, msg);
	    }*/
	    
	    // We have a common expcontext xml for tissueExpression and rnaExpression
	    val dataset =  (tissueExpression \ "data").map(HPAExpcontextUtil.createTissueExpressionLists(_)).flatten.toSet;
	    // We exclude celllines data from the rnaExpression
	    val datasetrna =  (rnaExpression \ "data").filter(el => (el \ "tissue").text != "").map(HPAExpcontextUtil.createTissueExpressionLists(_)).flatten.toSet;
	    val alldata = dataset ++ datasetrna
	    new TissueExpressionDataSet(alldata);
	    
  }

}