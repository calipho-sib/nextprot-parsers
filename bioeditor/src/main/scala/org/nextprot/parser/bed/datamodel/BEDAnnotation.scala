package org.nextprot.parser.bed.datamodel



case class BEDAnnotation(val accession : String, val _subject: String, val _relation: String, val _object : String, val _evidences: List[BEDEvidence]) {
 
   def isVP () : Boolean = {
     return accession.contains("CAVA-VP");
   }
 
  
  def isValid () : Boolean = {
    if(_subject.startsWith("BRCA2")){
      return true;
    }else return false;
  }
  
}