
package org.nextprot.parser.hpa.commons.rules

import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.core.exception.NXException


case class APEQualityRule(reliability: HPAReliabilityValue) {

/* 
 *  See new specs in issue NEXTPROT-1383 
 * TODO: provide a link to specs for new rule, rename method and file IntegratedQualityRule, maybe rename the class
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