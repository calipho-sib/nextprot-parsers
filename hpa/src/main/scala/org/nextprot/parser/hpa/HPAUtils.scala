package org.nextprot.parser.hpa

import scala.xml.NodeSeq
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_EXPR
import org.nextprot.parser.hpa.subcell.cases.CASE_MULTIPLE_UNIPROT_MAPPING
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL
import org.nextprot.parser.hpa.constants.HPAValidationValue
import org.nextprot.parser.hpa.constants.HPAValidationValue._

object HPAUtils {

  /**
   * Returns the 'enum' value for Western blot validation for a given antibody (Supportive, Uncertain, Not supportive)
   */
  def getWesternBlot(antibodyElem: NodeSeq): HPAValidationValue = {
    val HPAwbText = (antibodyElem \ "westernBlot" \ "verification").text;
    //In case there is no western blot experiment we use uncertain for western blot
    if (HPAwbText.isEmpty()) {
      Stats.increment("COMPLEMENT-SPECS", "western blot missing => uncertain")
      return HPAValidationValue.withName("uncertain")
    } else {
      return HPAValidationValue.withName(HPAwbText)
    }

  }

  /**
   * Selects the tissueExpression element having assayType=tissue
   */
  def getTissueExpressionNodeSeq(entryElem: NodeSeq): NodeSeq = {
    return (entryElem \ "tissueExpression").filter(el => (el \ "@assayType").text == "tissue")
  }

  /**
   * Returns the summary description related to the tissue expression
   */
  def getTissueExpressionSummary(entryElem: NodeSeq): String = {
    return (getTissueExpressionNodeSeq(entryElem) \ "summary").text
  }

  /**
   * Returns the 'enum' value for Protein array validation for a given antibody (Supportive, Uncertain, Not supportive)
   */
  def getProteinArray(antibodyElem: NodeSeq): HPAValidationValue = {
    val antibodyName = (antibodyElem \ "@id").text;
    //In case of CAB it is always supportive

    if (antibodyName.startsWith("CAB")) {
      Stats.increment("COMPLEMENT-SPECS", "CAB antibodies as Supportive")
      HPAValidationValue.withName("supportive");
    } else {
      return HPAValidationValue.withName((antibodyElem \ "proteinArray" \ "verification").text);

    }
  }

  def getEnsgId(entryElem: NodeSeq): String = {
    (entryElem \ "identifier" \ "@id").text;
  }

  def getAccession(entryElem: NodeSeq): String = {
    if ((entryElem \ "identifier" \ "xref" \\ "@id").size > 1)
      throw new NXException(CASE_MULTIPLE_UNIPROT_MAPPING);

    val accession = (entryElem \ "identifier" \ "xref" \ "@id").text;
    if (accession.isEmpty()) {
      // Try to retrieve mapping another way
      return get_ENSG_To_NX_accession((entryElem \ "identifier" \ "@id").text)
    } else return accession;
  }

  // according to Anne, an entry might contain more than one uniprot AC: cases exist !
  def getAccessionList(entryElem: NodeSeq): List[String] = {
    val uids = (entryElem \ "identifier" \ "xref")
    uids.map(a => (a \ "@id").text).toList
  }

  def isSelectedTreatedAsAPEForSubcell(entryElem: NodeSeq): Boolean = {
    val abtype = (entryElem \ "subcellularLocation" \ "@type").text.toLowerCase();
    if (abtype == "selected") { //check that is selected
      val selectableAbs = (entryElem \ "antibody").filter(a => !(a \ "subcellularLocation").isEmpty) // check that has subcellar location information for more than one antibody
      if (selectableAbs.length > 1) {
        return true
      }
    }
    return false

  }

  def getAntibodyIdListForSubcellular(entryElem: NodeSeq): List[String] = {
    val abs = (entryElem \ "antibody").filter(a => !(a \ "subcellularLocation").isEmpty);
    
    // we expect one or more antibodies matching the criteria above
    val list = abs.map(a => (a \ "@id").text).toList
    if (list.size == 0) throw new NXException(CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL)
    return list

  }

  //return antibodies which have 1 element <tissueExpression assayType=tissue...>
  def getAntibodyIdListForExpr(entryElem: NodeSeq): List[String] = {
    val abs = (entryElem \ "antibody").filter(ab => {
      var count = 0
      val tes = (ab \ "tissueExpression").foreach(te => {
        if ((te \ "@assayType").text == "tissue") count = count + 1
      })
      count == 1
    })
    // we expect one or more antibodies matching the criteria above
    val list = abs.map(a => (a \ "@id").text).toList
    if (list.size == 0) throw new NXException(CASE_NO_ANTIBODY_FOUND_FOR_EXPR)
    return list

  }

  // TODO: check with Anne / Paula
  def getTissueExpressionType(entryElem: NodeSeq): String = {
    val hpaType = (getTissueExpressionNodeSeq(entryElem) \ "@type").text.toLowerCase()
    hpaType match {
      case "ape" => "integrated"
      case "single" => "single"
      case "selected" => "selected"
      case _ => throw new Exception("Unexpected tissue expression type: " + hpaType)
    }
  }

  // TODO: check with Anne / Paula
  def getSubcellIntegrationType(entryElem: NodeSeq): String = {
    val hpaType = (entryElem \ "subcellularLocation" \ "@type").text.toLowerCase()
    hpaType match {
      case "ape" => "integrated"
      case "single" => "single"
      case "selected" => "selected"
      case _ => throw new Exception("Unexpected subcell type: " + hpaType)
    }
  }

  private def get_ENSG_To_NX_accession(identifier: String): String = {
    // TODO: write the code and put it in the core section or somewhere else but not specific for HPA
    return ""
  }

}