package org.nextprot.parser.hpa.commons

import org.scalatest._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue._

class HPAValidationIntegratedValueTest extends FlatSpec with Matchers {

  it should " get a SupportiveAll if all values are Supportive" in {
    val integratedValue = HPAValidationIntegratedValue.integrate(List(Supportive, Supportive));
    assert(integratedValue == SupportiveAll)
  }

  it should " get SupportiveAll if there is only one value and this value is Supportive " in {
    val integratedValue = HPAValidationIntegratedValue.integrate(List(Supportive));
    assert(integratedValue == SupportiveAll)
  }

  it should " get a SupportiveOne if there is only one Supportive among other values " in {
    val integratedValue = HPAValidationIntegratedValue.integrate(List(Supportive, Uncertain, Supportive, NotSupportive));
    assert(integratedValue == SupportiveOne)
  }

  it should " get a UncertainAll if all values are uncertains " in {
    val integratedValue = HPAValidationIntegratedValue.integrate(List(Uncertain, Uncertain));
    assert(integratedValue == UncertainAll)
  }

  it should " get a UncertainOne if all one is uncertain among not supportive " in {
    val integratedValue = HPAValidationIntegratedValue.integrate(List(Uncertain, NotSupportive));
    assert(integratedValue == UncertainOne)
  }

  it should " get a NotSupportiveAll if there is only one value and this value is not supportive " in {
    val integratedValue = HPAValidationIntegratedValue.integrate(List(NotSupportive));
    assert(integratedValue == NotSupportiveAll)
  }

  it should " get a NotSupportiveAll if all are not supprotive " in {
    val integratedValue = HPAValidationIntegratedValue.integrate(List(NotSupportive));
    assert(integratedValue == NotSupportiveAll)
  }

}
