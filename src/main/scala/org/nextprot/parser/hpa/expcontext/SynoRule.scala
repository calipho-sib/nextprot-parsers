package org.nextprot.parser.hpa.expcontext

import scala.xml.Node;
import scala.xml.NodeSeq

class SynoRule(val syno: String, val rule: String) extends Ordered[SynoRule] {

	override def equals(that: Any): Boolean = that match {
    	case other: SynoRule => syno == other.syno && rule == other.rule;
    	case _ => false
    }
	override def hashCode = (syno + "/" + rule).hashCode(); 
	
	override def toString(): String = {
		return "(synonym:" + syno + ", rule:" + rule + ")";	  
	}
	
	override def compare(that: SynoRule) :Int = {
	  return (syno + "/" + rule).compare(that.syno + "/" + that.rule)
	} 
}


