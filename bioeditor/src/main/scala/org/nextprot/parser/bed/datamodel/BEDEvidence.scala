package org.nextprot.parser.bed.datamodel

import org.nextprot.parser.bed.utils.BEDUtils

case class BEDEvidence(val _subject : String, val _relation : String, val _object : String, val isNegative: Boolean, val vdAlleles : List[String], val references: List[(String, String)]) {

  def getReferences : List[(String, String)] = {
    return references;
  }
    
  def getTermAttributeRelation() : (String, String) = {
    return BEDUtils.getTermAndAttribute(_relation, isNegative);
  }
  
  def getRealSubject() : String = {
    if(vdAlleles.size > 1){
    	return vdAlleles.sortWith(_ > _).mkString(" + "); //TODO add allels
    }else {
      return _subject;
    }
  }

}
