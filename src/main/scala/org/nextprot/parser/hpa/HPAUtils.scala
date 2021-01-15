package org.nextprot.parser.hpa

import scala.xml.{Node, NodeSeq}
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_EXPR
import org.nextprot.parser.hpa.subcell.cases.CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL

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

  def getAntibodyIdListForSubcellular(entryElem: NodeSeq): List[String] = {
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

  // RNAseq data
  def getRnaExpression(entryElem: NodeSeq, assayType: String, tagName: String): Map[String, String] = {
    var hasExpression: Boolean = false

    val rnaExpressionMap = ((entryElem \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == assayType) })
          \ "data" filter dataToNotExclude(tagName))
      .map(f => ((f \ tagName).text, HPAUtils.getCheckedRNALevel(f \ "level")))
      .toMap

    return rnaExpressionMap
  }

  // scRNAseq data
  def getScRnaExpression(entryElem: NodeSeq): Map[String, String] = {
    var hasExpression: Boolean = false

    val expressionMap = ((entryElem \ "cellTypeExpression") \ "singleCellTypeExpression")
      .map(f => ((f \\ "@name").text, HPAUtils.getCheckedRNALevel(f)))
      .toMap

    return expressionMap
  }

  // Return true if data should not be excluded.
  // - we want to exclude tissues 1) without attribute 'organ' or 2) with attribute 'organ' equals to Blood
  def dataToNotExclude(tagName: String)(dataNode: Node): Boolean = {

    var notToExclude: Boolean = true;
    if (tagName.equals("tissue")) {
      val filteredData = (dataNode \ tagName)
        .filter(t => t.attributes("organ") != null)
        .filter(t => !(t exists { _ \\ "@organ" exists (_.text == "Blood")}));
      notToExclude = filteredData.nonEmpty;
    }
    return notToExclude;
  }

  def getTissueExpressionType(entryElem: NodeSeq): String = {
     // Used to be a rule based on antibody status, but since HPA17 the  @type attribute has disappeared
    "integrated" 
  }

  def getSubcellIntegrationType(entryElem: NodeSeq): String = {
    // Used to be a rule based on antibody status, but since HPA17 the  @type attribute has disappeared
    "integrated" 
  }

	def getCheckedLevel(someLevel: String): String = {
		val level = someLevel.toLowerCase();
		if (level == "not detected") return level;
		if (level == "negative") return level;
		if (level == "positive") return level;
		if (level == "low") return level;
		if (level == "medium") return level;
		if (level == "high") return level;
		throw new Exception("Unexpected expression level value:" + someLevel )
	}

  def getCheckedRNALevel(levelNodes: NodeSeq): String = {
    val normalizedLevel =((levelNodes filter { _ \\ "@type" exists (_.text == "normalizedRNAExpression") }) \ "@expRNA");
    if (normalizedLevel == null || normalizedLevel.isEmpty) {
      throw new Exception("No normalized expression level value: " + levelNodes)
    }
    val level = normalizedLevel.toString().toFloat;
    if (level < 1) return "not detected"
    return "detected"
  }

  private def get_ENSG_To_NX_accession(identifier: String): String = {
    // TODO: write the code and put it in the core section or somewhere else but not specific for HPA
    // At present this step is performed by Anne at the loading stage
    return ""
  }

}