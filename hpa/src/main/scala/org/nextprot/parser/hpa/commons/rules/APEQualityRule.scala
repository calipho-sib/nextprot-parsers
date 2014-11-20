
package org.nextprot.parser.hpa.commons.rules

import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue._
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.stats.Stats


case class APEQualityRule(reliability: HPAReliabilityValue, hpaPA: HPAValidationIntegratedValue, hpaWB: HPAValidationIntegratedValue) {

/* 
 * See specs at https://docs.google.com/spreadsheets/d/1nZmb6TXEMMFdtUtCi-2EpzUNkkeohDle3KMwfLfvjdg/edit?usp=sharing
 * sheet v.13
 */
  
  /** 
   *  Current status of version v.13, done with Paula 21.11.2014
   */
  def getQuality: NXQuality = {
    val res: NXQuality =
     APEQualityRule.this match {
      case APEQualityRule(Supportive, SupportiveAll, SupportiveAll) => GOLD     // rule # G1
      case APEQualityRule(Supportive, SupportiveAll, BestIsSupportive) => GOLD   // rule # G2
      case APEQualityRule(Uncertain, UncertainAll,_) => BRONZE					// rule # B6
      case APEQualityRule(Uncertain, NotSupportiveAll, _) => BRONZE             // rule # N1 (exception thrown before arriving here, see CASE_NO_RULE*)
      case APEQualityRule(NotSupportive, _, _) => BRONZE  						// rule # B5
      case _ => SILVER
    }
    res match {
      case GOLD => Stats ++ ("RULES_FOR_GOLD", toString())
      case SILVER => Stats ++ ("RULES_FOR_SILVER", toString())
      case BRONZE => Stats ++ ("RULES_FOR_BRONZE", toString())
    }
    return res
  }
  
}