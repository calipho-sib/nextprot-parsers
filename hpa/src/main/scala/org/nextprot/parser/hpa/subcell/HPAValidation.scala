package org.nextprot.parser.hpa.subcell

import scala.xml.NodeSeq
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue.HPAValidationValue
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import java.io.File
import org.nextprot.parser.hpa.HPAConfig
import org.nextprot.parser.core.stats.StatisticsCollectorSingleton

object HPAValidation {

  /**
   * preconditions for tissue expression
   */
  def checkPreconditionsForExpr(entryElem: NodeSeq) = {

    // We always get one <tissueExpression typeAssay="tissue" technology="IH"... > from HPA but...
	checkMainTissueExpression(entryElem)

  }

  def checkMainTissueExpression(entryElem: NodeSeq) = {
    val tesok = (entryElem \ "tissueExpression").
      filter(el => (el \ "@assayType").text == "tissue" && (el \ "@technology").text == "IH")
    if (tesok.size != 1) throw new NXException(CASE_ASSAY_TYPE_NOT_TISSUE)
  }

  /**
   *
   */
  def checkPreconditions(accession: String, entryElem: NodeSeq) = {

    if (accession.isEmpty()) throw new NXException(CASE_NO_UNIPROT_MAPPING);

    val locations = (entryElem \ "subcellularLocation" \ "data" \ "location").text;
    if (locations.isEmpty()) {
      throw new NXException(CASE_NO_SUBCELLULAR_LOCATION_DATA);
    } else {
      if ((entryElem \ "subcellularLocation" \ "summary").text == "The protein was not detected.")
        throw new NXException(PROTEIN_NOT_DETECTED_BUT_LOCATION_EXISTENCE)
    }

    if (!isValidForCellLines(entryElem)) {
      throw new NXException(CASE_RNA_NOT_DETECTED);
    }

  }

  private def isValidForCellLines(entryElem: NodeSeq): Boolean = {
    val rnamap = (entryElem \ "rnaExpression" \ "data").map(f => ((f \ "cellLine").text, (f \ "level").text)).toMap;
    val cellLineList = (entryElem \ "antibody" \ "subcellularLocation" \ "subAssay" \ "data" \ "cellLine").toList
    var isValid: Boolean = false

    if (rnamap.isEmpty) {
      StatisticsCollectorSingleton.increment("COMPLEMENT-SPECS", "RNA is missing")
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