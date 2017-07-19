package org.nextprot.parser.hpa.subcell

import scala.xml.NodeSeq
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import java.io.File
import org.nextprot.parser.hpa.HPAConfig
import org.nextprot.parser.core.stats.Stats

object HPAValidation {

  /**
   * preconditions for antibodies
   */
  def checkPreconditionsForAb(entryElem: NodeSeq) = {

    // We always get one <tissueExpression typeAssay="tissue" technology="IHC"... > from HPA but...
	checkMainTissueExpression(entryElem, "antibody")

  }

  /**
   * preconditions for tissue expression
   */
  def checkPreconditionsForExpr(entryElem: NodeSeq) = {

    // We always get one <tissueExpression typeAssay="tissue" technology="IHC"... > from HPA but...
	checkMainTissueExpression(entryElem, "expression")

  }

  def checkMainTissueExpression(entryElem: NodeSeq, filter: String) = {
    val tesok = (entryElem \ "tissueExpression").
      filter(el => (el \ "@assayType").text == "tissue" && (el \ "@technology").text == "IHC")
      Stats ++ ("CHECKING_TISSUE", "assayType")

    if (tesok.size != 1) {
      Stats ++ ("CASE_ASSAY_TYPE_NOT_TISSUE", "not tissue")
      throw new NXException(CASE_ASSAY_TYPE_NOT_TISSUE)
    }
    
    if(filter == "expression" && (tesok  \ "data").size == 0) { // eg: ENSG00000005981, ENSG00000000005
      Stats ++ ("CASE_NO_TISSUE_DATA_FOR_ENTRY_LEVEL", "no data")
      throw new NXException(CASE_NO_TISSUE_DATA_FOR_ENTRY_LEVEL)
    }
    
  }

  /**
   * preconditions for RNA tissue expression
   */
  def checkPreconditionsForRnaExpr(accession: String, entryElem: NodeSeq) = {
   //if (accession.isEmpty()) throw new NXException(CASE_NO_UNIPROT_MAPPING);
  }

  /**
   *
   */
  def checkPreconditionsForSubcell(accession: String, entryElem: NodeSeq) = {

    //if (accession.isEmpty()) throw new NXException(CASE_NO_UNIPROT_MAPPING);

    val locations = (entryElem \ "cellExpression" \ "data" \ "location").text;
    if (locations.isEmpty()) {
      throw new NXException(CASE_NO_SUBCELLULAR_LOCATION_DATA);
    } else {
      if ((entryElem \ "cellExpression" \ "summary").text == "The protein was not detected.")
        throw new NXException(PROTEIN_NOT_DETECTED_BUT_LOCATION_EXISTENCE)
    }

    if (!isValidForCellLines(entryElem)) { // Must have RNA detected for at least one cell line ?
      throw new NXException(CASE_RNA_NOT_DETECTED);
    }

  }

  private def isValidForCellLines(entryElem: NodeSeq): Boolean = {
    val rnamap = (entryElem \ "rnaExpression" \ "data").map(f => ((f \ "cellLine").text, (f \ "level").text)).toMap;
    val cellLineList = (entryElem \ "antibody" \ "cellExpression" \ "subAssay" \ "data" \ "cellLine").toList
    var isValid: Boolean = false

    if (rnamap.isEmpty) {
      Stats ++ ("COMPLEMENT-SPECS", "RNA for cell lines is missing")
      return true // No cell line data for antibodies or No RNAseq data
    }
    cellLineList.foreach(cellLine => {
      //Check only for cell lines
      if (rnamap.contains(cellLine.text)) {
        //At least one must be not "not detected" (at least one must be detected)
        isValid |= (rnamap(cellLine.text) != "Not detected")
      }
    })
    return isValid
  }

}