package org.nextprot.parser.hpa.expcontext

import scala.xml.Node

object HPAExpcontextUtil {

  // creates a list of TissueExpressionData from the HPA XML file element tissueExpression data 
	// TODO: we get several level type values: staining, expression (,...?), is it ok ? take data
	// TODO: from antibody if selected / single ???
	def createTissueExpressionLists(el: Node) :List[TissueExpressionData] = {
	    val tissue:String = (el \ "tissue").text
		val tcs = (el \ "tissueCell")
		if (tcs.size==0) {
			//throw new Exception("tissue expression data without tissueCell element")
			// see where is stored the expression level in this case
		    return List(new TissueExpressionData(tissue,null,null));
		} else {
			return tcs.map(x => {
			  val ct = (x \ "cellType").text
			  val lt = (x \ "level" \ "@type").text
			  //if (lt != "staining") throw new Exception("unexpected tissue level type: " + lt)
			  val lv = (x \ "level").text
			  new TissueExpressionData(tissue, ct, lv)
			}).toList;
		}
	}

	
	// eco->spatial pattern of protein expression evidence;tissue->liver;
	// eco->spatial pattern of protein expression evidence;tissue->adrenal gland;cell type->glandular cells;
	def getSynonymForXml(ted: TissueExpressionData) : String = {
		val cleanTissue = ted.tissue.replace(";", ",").toLowerCase()
		if (ted.cellType==null) {
			"eco->spatial pattern of protein expression evidence;tissue->"+ cleanTissue +";"
		} else {
			"eco->spatial pattern of protein expression evidence;tissue->"+ cleanTissue +";cell type->"+ ted.cellType.toLowerCase() +";"
		}
	}
	
	// for mapping caloha: lower case, removes trailing digits and spaces
    def getCleanTissue(s:String): String = {
    	val pattern = "\\s+[0-9]*\\s*$".r;
    	val res = pattern replaceFirstIn(s, "");
    	return res.toLowerCase();
    }

    // for mapping caloha: lower case, replaces some patterns with some strings
    def getCleanCellType(s: String): String = {
		val frlist = List(
		   ("lymphoid cells outside reaction centra".r,"lymphoid cell"),
		   ("pneumocytes".r,"pneumocyte"),
		   ("ovarian stroma cells".r,"ovarian stromal cells"),
		   ("myocytes".r,"myocyte"));
		var res = s.toLowerCase();
		frlist.foreach(el => res = el._1 replaceFirstIn(res,el._2));
		return res;
    }


	
    // for mapping caloha: try to match with ti + ct first and then with ct only 
    def getCalohaMapping(ted: TissueExpressionData,
        syn2tissueMap:scala.collection.immutable.Map[String, CalohaMapEntry]) :CalohaMapEntry  = {
		val ti = getCleanTissue(ted.tissue)
    	if (ted.cellType==null) {
    		syn2tissueMap.get(ti) match {
    			case Some(cme) => { return cme }
    			case None =>  { return null  }
    		}
    	} else {
    		val ct = getCleanCellType(ted.cellType);
    		syn2tissueMap.get(ti + " " + ct) match {
    			case Some(cme) => { return cme  }
    			case None => {
    			  syn2tissueMap.get(ct) match {
    			  	case Some(cme) => { return cme }
    			    case None => { return null }
    			  }
    			}
    		}
    	}
    }
    
	
	
}