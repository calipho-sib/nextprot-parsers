package org.nextprot.parser.bed.datamodel

case class BEDCV(val accession: String, val category: String, val name: String) {
  
  def getApiCategory : String = {
    if(category.equals("Gene Ontology")) {
     "go molecular function"
    } else {
      "";
    }
  }
}