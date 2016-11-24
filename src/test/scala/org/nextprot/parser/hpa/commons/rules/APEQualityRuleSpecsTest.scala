package org.nextprot.parser.hpa.commons.rules

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue

class APEQualityRuleSpecsTest extends FlatSpec with Matchers {
  
  it should "get GOLD when reliability is Supportive and there is a supportive for both of the others" in {
	  assert(APEQualityRuleSpecs(HPAReliabilityValue.Supportive,
	      List(Supportive, Supportive),
	      List(Supportive, NotSupportive)).getQuality == NXQuality.GOLD)
  }

}