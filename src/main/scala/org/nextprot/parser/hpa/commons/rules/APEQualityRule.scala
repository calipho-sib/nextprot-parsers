
package org.nextprot.parser.hpa.commons.rules

import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.core.exception.NXException


//case class APEQualityRule(reliability: HPAReliabilityValue, hpaPA: HPAValidationIntegratedValue, hpaWB: HPAValidationIntegratedValue) {
case class APEQualityRule(reliability: HPAReliabilityValue) {

/* 
 * See specs at https://docs.google.com/spreadsheets/d/1nZmb6TXEMMFdtUtCi-2EpzUNkkeohDle3KMwfLfvjdg/edit?usp=sharing
 * sheet v.13
 *  See new specs in issue NEXTPROT-1383 
 * TODO: provide a link tp specs for new rule
 */
  
  def getQuality: NXQuality = { // Current status of version v.16.1, new QC rules checked with Paula
    val res: NXQuality =
     APEQualityRule.this match {
      case APEQualityRule(Supportive) => GOLD     // rule # G?
      case APEQualityRule(Uncertain) => SILVER					// rule # G?
      case _ => BRONZE
    }
    return res
  }
  
}