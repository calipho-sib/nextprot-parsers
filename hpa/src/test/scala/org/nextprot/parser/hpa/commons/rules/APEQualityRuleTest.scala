package org.nextprot.parser.hpa.commons.rules

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue._
import org.nextprot.parser.core.constants.NXQuality

class APEQualityRuleTest extends FlatSpec with Matchers {


  
  it should "get GOLD when reliability is Supportive and at least one supportive for PA and WB" in {
	assert(APEQualityRule(Supportive,SupportiveAll,SupportiveAll).getQuality === NXQuality.GOLD);
	assert(APEQualityRule(Supportive,SupportiveAll,BestIsSupportive).getQuality === NXQuality.GOLD);
	assert(APEQualityRule(Supportive,SupportiveAll,UncertainAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,SupportiveAll,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,SupportiveAll,NotSupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,SupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,UncertainAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,NotSupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,UncertainAll,SupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,UncertainAll,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,UncertainAll,UncertainAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,UncertainAll,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,UncertainAll,NotSupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,SupportiveAll,SupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,SupportiveAll,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,SupportiveAll,UncertainAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,SupportiveAll,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,SupportiveAll,NotSupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,SupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,UncertainAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,NotSupportiveAll).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,UncertainAll,SupportiveAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,UncertainAll,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,UncertainAll,UncertainAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,UncertainAll,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,UncertainAll,NotSupportiveAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,SupportiveAll,SupportiveAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,SupportiveAll,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,SupportiveAll,UncertainAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,SupportiveAll,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,SupportiveAll,NotSupportiveAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,SupportiveAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,UncertainAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,NotSupportiveAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,UncertainAll,SupportiveAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,UncertainAll,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,UncertainAll,UncertainAll).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,UncertainAll,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,UncertainAll,NotSupportiveAll).getQuality === NXQuality.BRONZE);
    
    
  }

}