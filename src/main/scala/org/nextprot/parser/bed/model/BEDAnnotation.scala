package org.nextprot.parser.bed.model

case class BEDAnnotation(val accession: String, val _subject: String, val _relation: String, val _objectTerm: BEDCV, val _bioObject: String, val _evidences: List[BEDEvidence]) {
  
}