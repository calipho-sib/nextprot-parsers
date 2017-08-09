package org.nextprot.parser.hpa

import scala.xml.NodeSeq
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_EXPR
import org.nextprot.parser.hpa.subcell.cases.CASE_MULTIPLE_UNIPROT_MAPPING
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_RULE_FOR_PA_NOT_SUPPORTIVE

object HPAUtils {

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

  def getEnsgId(entryElem: NodeSeq): String = {
    (entryElem \ "identifier" \ "@id").text;
  }

  // according to Anne, an entry might contain more than one uniprot AC: cases exist !
  def getAccessionList(entryElem: NodeSeq): List[String] = {
    val uids = (entryElem \ "identifier" \ "xref")
    uids.map(a => (a \ "@id").text).toList
  }

  def isSelectedTreatedAsAPEForSubcell(entryElem: NodeSeq): Boolean = {
    val abtype = (entryElem \ "cellExpression" \ "@type").text.toLowerCase();
    if (abtype == "selected") { //check that is selected
      //val selectableAbs = (entryElem \ "antibody").filter(a => !(a \ "subcellularLocation").isEmpty) // check that has subcellar location information for more than one antibody
      val selectableAbs = (entryElem \ "antibody").filter(a => !(a \ "cellExpression").isEmpty) // check that has subcellar location information for more than one antibody
      if (selectableAbs.length > 1) {
        return true
      }
    }
    return false

  }

  def getAntibodyIdListForSubcellular(entryElem: NodeSeq): List[String] = {
    //val abs = (entryElem \ "antibody").filter(a => !(a \ "subcellularLocation").isEmpty);
    val abs = (entryElem \ "antibody").filter(a => !(a \ "cellExpression").isEmpty);
    
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

   // new rnaseq data
    def getTissueRnaExpression(entryElem: NodeSeq): Map[String, String] = {
    var isValid: Boolean = false
    val rnatissuemap = (entryElem \ "rnaExpression" \ "data").map(f => ((f \ "tissue").text, (f \ "level").text)).toMap.drop(1); 
    rnatissuemap foreach (x => isValid |= (x._2 != "Not detected") )
    if(!isValid) Console.err.println("all of them 'Not detected'")
    return rnatissuemap
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
    //val hpaType = (entryElem \ "subcellularLocation" \ "@type").text.toLowerCase()
    val hpaType = (entryElem \ "cellExpression" \ "@type").text.toLowerCase()
    hpaType match {
      case "ape" => "integrated"
      case "single" => "single"
      case "selected" => "selected"
      case _ => throw new Exception("Unexpected subcell type: " + hpaType)
    }
  }

  private def get_ENSG_To_NX_accession(identifier: String): String = {
    // TODO: write the code and put it in the core section or somewhere else but not specific for HPA
    // At present this step is performed by Anne at the loading stage
    return ""
  }

}