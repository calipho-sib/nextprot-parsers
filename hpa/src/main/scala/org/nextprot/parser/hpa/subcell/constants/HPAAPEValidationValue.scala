package org.nextprot.parser.hpa.subcell.constants

import org.nextprot.parser.hpa.constants.HPAValidationValue.HPAValidationValue
import org.nextprot.parser.hpa.constants.HPAValidationValue._


//TODO where is this should be put???
/**
 * For new rules 2014
 */
object HPAAPEValidationValue extends Enumeration {
  type HPAAPEValidationValue = Value
  val SupportiveAll, SupportiveOne, UncertainAll , UncertainOne , Not_Supportive = Value

  /**
   * Transforms value of XML to object enumeration
   */
  final def integrate(values: List[HPAValidationValue])(implicit dummy: DummyImplicit): HPAAPEValidationValue = {
    
    val sup = values.filter(_ == Supportive).size
    if (sup == values.size) return SupportiveAll
    if (sup >= 1) return SupportiveOne
    val unc = values.filter(_  == Uncertain).size
    if (unc == values.size) return HPAAPEValidationValue.UncertainAll
    if (unc >= 1) return HPAAPEValidationValue.UncertainOne
    return HPAAPEValidationValue.Not_Supportive

  }
}
