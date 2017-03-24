package org.nextprot.parser.hpa
import scala.xml.NodeSeq
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import org.nextprot.parser.hpa.commons.rules.APEQualityRule
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._

object HPAQuality {
  
  
  /*
   * Returns the global quality for sub cellular location or tissue expression
   * @section = tissuueExpression or subcellularLocation
   */
  def getQuality(entryElem: NodeSeq, section: String): (NXQuality, String) = {
    val abtype = (entryElem \ section \ "@type").text.toLowerCase();

    abtype match {

      case "single" => {
        Stats ++ ("ENTRIES-TYPE", "single");
        return getQualityForOneAntibody(entryElem, entryElem \ "antibody", section)
      }

      case "selected" => {

        if ((section == "subcellularLocation") && HPAUtils.isSelectedTreatedAsAPEForSubcell(entryElem)) {
          Stats ++ ("ENTRIES-TYPE", "selected but treated as ape");
          return getQualityForIntegratedAntibody(entryElem, section)
        } else {

          //Simple check that makes sure that there is only one antibody (selected)
          val antibodySelected = (entryElem \ "antibody").filter(a => !(a \ section).isEmpty)
          if (antibodySelected.length != 1) {
            throw new NXException(CASE_MORE_THAN_ONE_ANTIBODY_FOUND_FOR_SELECTED, antibodySelected.length + " antibodies ")
          }

          Stats ++ ("ENTRIES-TYPE", "ape");
          return getQualityForOneAntibody(entryElem, antibodySelected, section)

        }
      }

      case "ape" => {
        Stats ++ ("ENTRIES-TYPE", "ape");
        return getQualityForIntegratedAntibody(entryElem, section)
      }
      case _ => {
        Stats ++ ("ENTRIES-TYPE", "unknown: " + abtype);
        throw new NXException(CASE_IFTYPE_UNKNOWN, abtype + " not found")
      }

    }

  }
  
  def getReliabilityScore(entryElem: NodeSeq, section: String) : HPAReliabilityValue = {
     //Extract experiment reliability
     val res = HPAReliabilityValue withName (entryElem \ section \ "verification").text
     return res
  }

  /**
   * Returns the quality for the single and selected case
   */
  def getQualityForOneAntibody(entryElem: NodeSeq, antibodyElem: NodeSeq, section: String): (NXQuality, String)  = {

    val reliability = getReliabilityScore(entryElem, section)
    val pa = HPAUtils.getProteinArray(antibodyElem)
    val wb = HPAUtils.getWesternBlot(antibodyElem)
    
    val rule = new APEQualityRule(reliability, 
    				   HPAValidationIntegratedValue.integrate(List(pa)), 
    				   HPAValidationIntegratedValue.integrate(List(wb)));
    
    return (rule.getQuality, rule.toString);


  }

  /**
   * Returns the quality for the APE case - version 2014
   * section can be either subcellularLocation or tissueExpression
   */
  def getQualityForIntegratedAntibody(entryElem: NodeSeq, section: String): (NXQuality, String) = {

    //Extract experiment reliability
    val pa = getAPEPtroteinArrayQuality(entryElem, section)
    val wb = getAPEWesternBlotQuality(entryElem, section)
    val reliability = getReliabilityScore(entryElem, section)

    val rule = new APEQualityRule(reliability, pa, wb);
    return (rule.getQuality, rule.toString);

  }

  /**
   * Returns one out of the five values enumerated in HPAValidationIntegratedValue
   */
  def getAPEPtroteinArrayQuality(entryElem: NodeSeq, section: String): HPAValidationIntegratedValue = {
       val values = (entryElem \ "antibody").filter(el => !(el \ section).isEmpty).toList.map(ab => HPAUtils.getProteinArray(ab)).toList;
       return HPAValidationIntegratedValue.integrate(values);
  }

  /**
   * Returns one out of the five values enumerated in HPAValidationIntegratedValue
   */
  def getAPEWesternBlotQuality(entryElem: NodeSeq, section: String): HPAValidationIntegratedValue = {
       val values = (entryElem \ "antibody").filter(el => !(el \ section).isEmpty).toList.map(ab => HPAUtils.getWesternBlot(ab)).toList;
       return HPAValidationIntegratedValue.integrate(values);
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