package org.nextprot.parser.nx.datamodel

import org.nextprot.parser.bed.utils.BEDUtils

case class BEDEvidence(val _annotationAccesion : String, val _subject : String, val _relation : String, val _objectTerm : BEDCV, val _bioObject : String, val isNegative: Boolean, val vdAlleles : List[String], val references: List[(String, String)]) {

  def getReferences : List[(String, String)] = {
    return references;
  }
  
  def getRealObject() : String = {
    return _objectTerm.name + _bioObject;
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
