package org.nextprot.parser.bed.datamodel


case class BEDEvidence(val isNegative: Boolean) {
  
}

case class BEDAnnotation(val _subject: String, val _relation: String, val _object : String, val _evidences: List[BEDEvidence]) {
  
  
  def isValid () : Boolean = {
    if(_subject.startsWith("BRCA2")){
      return true;
    }else return false;
    
  }
  
  

}