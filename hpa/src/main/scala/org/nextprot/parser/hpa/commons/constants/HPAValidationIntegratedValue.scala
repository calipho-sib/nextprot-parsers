package org.nextprot.parser.hpa.commons.constants

import org.nextprot.parser.hpa.commons.constants.HPAValidationValue.HPAValidationValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._


/**
 * Used to compute the 
 */
object HPAValidationIntegratedValue extends Enumeration {
  type HPAValidationIntegratedValue = Value
  val AllSupportive, BestIsSupportive, AllUncertain , BestIsUncertain , AllNotSupportive = Value

  /**
   * Transforms values 
   */
  final def integrate(values: List[HPAValidationValue])(implicit dummy: DummyImplicit): HPAValidationIntegratedValue = {
    
    val sup = values.filter(_ == Supportive).size
    if (sup == values.size) return AllSupportive
    
    if (sup >= 1) return BestIsSupportive
    val unc = values.filter(_  == Uncertain).size
    
    if (unc == values.size) return AllUncertain
    if (unc >= 1) return BestIsUncertain
    
    return AllNotSupportive

  }
}
