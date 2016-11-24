package org.nextprot.parser.hpa.expcontext

import scala.xml.Node;
import scala.xml.NodeSeq

class TissueExpressionData(val tissue:String, val cellType:String, val level:String) {

	override def equals(that: Any): Boolean = that match {
    	case other: TissueExpressionData => tissue == other.tissue && cellType == other.cellType;
    	case _ => false
    }
	override def hashCode = (tissue + "/" + cellType).hashCode(); 
	
	override def toString(): String = {
		return "(tissue:" + tissue + ", cellType:" + cellType + ")";	  
	}
	def toStringWithLevel(): String = {
		return "(tissue:" + tissue + ", cellType:" + cellType + ", level:" + level + ")";	  
	}
}


