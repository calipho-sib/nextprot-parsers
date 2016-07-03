package org.nextprot.parser.bed.datamodel

case class BEDAnnotation(val accession: String, val _subject: String, val _relation: String, val _objectTerm: BEDCV, val _bioObject: String, val _evidences: List[BEDEvidence]) {

  def isVP(): Boolean = {
    return accession.contains("CAVA-VP");
  }
  def getAbsoluteObject(): String = {
    return _objectTerm + _bioObject;
  }
  
}