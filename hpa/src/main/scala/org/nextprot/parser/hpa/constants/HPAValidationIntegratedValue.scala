package org.nextprot.parser.hpa.constants

import org.nextprot.parser.hpa.constants.HPAValidationValue.HPAValidationValue
import org.nextprot.parser.hpa.constants.HPAValidationValue._


/**
 * Used to compute the 
 */
object HPAValidationIntegratedValue extends Enumeration {
  type HPAValidationIntegratedValue = Value
  val SupportiveAll, SupportiveOne, UncertainAll , UncertainOne , NotSupportiveAll = Value

  /**
   * Transforms values 
   */
  final def integrate(values: List[HPAValidationValue])(implicit dummy: DummyImplicit): HPAValidationIntegratedValue = {
    
    val sup = values.filter(_ == Supportive).size
    if (sup == values.size) return SupportiveAll
    
    if (sup >= 1) return SupportiveOne
    val unc = values.filter(_  == Uncertain).size
    
    if (unc == values.size) return UncertainAll
    if (unc >= 1) return UncertainOne
    
    return NotSupportiveAll

  }
}
