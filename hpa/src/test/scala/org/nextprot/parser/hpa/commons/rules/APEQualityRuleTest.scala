package org.nextprot.parser.hpa.commons.rules

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue._
import org.nextprot.parser.core.constants.NXQuality

class APEQualityRuleTest extends FlatSpec with Matchers {

  it should "get Gold when reliability is Supportive and at least one supportive for PA and WB" in {
    assert(APEQualityRule(Supportive, SupportiveAll, SupportiveAll).getQuality === NXQuality.GOLD);
    assert(APEQualityRule(Supportive, SupportiveAll, SupportiveOne).getQuality === NXQuality.GOLD);
    assert(APEQualityRule(Supportive, SupportiveOne, SupportiveAll).getQuality === NXQuality.GOLD);
    assert(APEQualityRule(Supportive, SupportiveOne, SupportiveOne).getQuality === NXQuality.GOLD);
  }

}