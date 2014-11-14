package org.nextprot.parser.hpa
import scala.xml.NodeSeq
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.constants.HPAValidationValue
import org.nextprot.parser.hpa.subcell.rules.AntibodyValidationRule
import org.nextprot.parser.hpa.constants.HPAAPEReliabilityValue._
import org.nextprot.parser.hpa.constants.HPAAPEReliabilityValue
import org.nextprot.parser.hpa.subcell.rules.APEQualityRule
import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue._
import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue
import org.nextprot.parser.core.stats.StatisticsCollectorSingleton

object HPAQuality {

  /**
   * Returns the global quality for sub cellular location or tissue expression
   * @section = tissuueExpression or subcellularLocation
   */
  def getQuality(entryElem: NodeSeq, section: String): NXQuality = {
    val abtype = (entryElem \ section \ "@type").text.toLowerCase();

    abtype match {

      case "single" => {
        StatisticsCollectorSingleton.increment("ENTRIES-TYPE", "single but treated as ape");
        return getQualityForIntegratedAntibody(entryElem, section)
      }

      case "selected" => {
        StatisticsCollectorSingleton.increment("ENTRIES-TYPE", "selected but treated as ape");
        return getQualityForIntegratedAntibody(entryElem, section)
      }

      case "ape" => {
        StatisticsCollectorSingleton.increment("ENTRIES-TYPE", "ape");

        return getQualityForIntegratedAntibody(entryElem, section)
        //return getQualityForIntegratedAntibody2014(entryElem, section) 
      }
      case _ => throw new NXException(CASE_IFTYPE_UNKNOWN, abtype + " not found")

    }

  }

  /**
   * Returns the quality for the single and selected case
   */
  def getQualityForOneAntibody(antibodyElem: NodeSeq, section: String): NXQuality = {

    // section = tissueExpression or subcellularLocation so far....
    val HPAVerifText =
      section match {
        case "subcellularLocation" => (antibodyElem \ section \ "subAssay" \ "verification").text
        // we may have multiple tissueExpression elements, keep only the one with asssayType = tissue
        case "tissueExpression" => {
          val oneTe = (antibodyElem \ section).filter(s => (s \ "@assayType").text == "tissue");
          (oneTe \ "verification").text
        }
        case _ => throw new Exception("section not expected: " + section)
      }

    val HPApa = HPAUtils.getProteinArray(antibodyElem);
    val HPAgl = HPAValidationValue.withName(HPAVerifText); // global HPA quality evaluation
    val HPAwb = HPAUtils.getWesternBlot(antibodyElem)

    new AntibodyValidationRule(HPApa, HPAgl, HPAwb).getQuality;

  }

  /**
   * Returns the quality for the APE case - version 2014 - not used yet
   * section can be either subcellularLocation or tissueExpression
   */
  def getQualityForIntegratedAntibody(entryElem: NodeSeq, section: String): NXQuality = {

    //TODO check section in te sl
    //Extract experiment reliability
    val reliabilityText = (entryElem \ section \ "verification").text;
    val reliability = HPAAPEReliabilityValue withName reliabilityText
    val pa = getAPEPtroteinArrayQuality(entryElem)
    val wb = getAPEWesternBlotQuality(entryElem)
    return new APEQualityRule(reliability, pa, wb).getQuality;

  }

  /**
   * Returns one out of the five values enumerated in HPAAPEValidationValue
   */
  def getAPEPtroteinArrayQuality(entryElem: NodeSeq): HPAAPEValidationValue = {
    val abs = (entryElem \ "antibody").toList
    val sup = abs.filter(ab => HPAUtils.getProteinArray(ab) == HPAValidationValue.Supportive).size
    if (sup == abs.size) return HPAAPEValidationValue.SupportiveAll
    if (sup >= 1) return HPAAPEValidationValue.SupportiveOne
    val unc = abs.filter(ab => HPAUtils.getProteinArray(ab) == HPAValidationValue.Uncertain).size
    if (unc == abs.size) return HPAAPEValidationValue.UncertainAll
    if (unc >= 1) return HPAAPEValidationValue.UncertainOne
    return HPAAPEValidationValue.Not_Supportive
  }

  /**
   * Returns one out of the five values enumerated in HPAAPEValidationValue
   */
  def getAPEWesternBlotQuality(entryElem: NodeSeq): HPAAPEValidationValue = {
    val abs = (entryElem \ "antibody").toList
    val sup = abs.filter(ab => HPAUtils.getWesternBlot(ab) == HPAValidationValue.Supportive).size
    if (sup == abs.size) return HPAAPEValidationValue.SupportiveAll
    if (sup >= 1) return HPAAPEValidationValue.SupportiveOne
    val unc = abs.filter(ab => HPAUtils.getWesternBlot(ab) == HPAValidationValue.Uncertain).size
    if (unc == abs.size) return HPAAPEValidationValue.UncertainAll
    if (unc >= 1) return HPAAPEValidationValue.UncertainOne
    return HPAAPEValidationValue.Not_Supportive
  }


  /**
   * Returns the score for one antibody
   */
  def getScoreForAntibody(antibodyElem: NodeSeq, section: String): Int = {

    val score = (antibodyElem \ "subcellularLocation" \ "subAssay" \ "data" \ "level").map(level =>
      level.text match {
        case "negative" => 0
        case "weak" => 1
        case "moderate" => 3
        case "strong" => 5
        case _ => throw new Exception("unexpected intensity value for " + section)
      }).sum

    return score;
  }

}