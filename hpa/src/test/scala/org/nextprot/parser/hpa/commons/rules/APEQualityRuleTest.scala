package org.nextprot.parser.hpa.commons.rules

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.hpa.commons.constants.HPAValidationIntegratedValue._
import org.nextprot.parser.core.constants.NXQuality

class APEQualityRuleTest extends FlatSpec with Matchers {


  
  it should "get GOLD when reliability is Supportive and at least one supportive for PA and WB" in {
	assert(APEQualityRule(Supportive,AllSupportive,AllSupportive).getQuality === NXQuality.GOLD);
	assert(APEQualityRule(Supportive,AllSupportive,BestIsSupportive).getQuality === NXQuality.GOLD);
	assert(APEQualityRule(Supportive,AllSupportive,AllUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,AllSupportive,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,AllSupportive,AllNotSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,AllSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,AllUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,BestIsSupportive,AllNotSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,AllUncertain,AllSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,AllUncertain,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,AllUncertain,AllUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,AllUncertain,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Supportive,AllUncertain,AllNotSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,AllSupportive,AllSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,AllSupportive,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,AllSupportive,AllUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,AllSupportive,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,AllSupportive,AllNotSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,AllSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,BestIsSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,AllUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,BestIsUncertain).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,BestIsSupportive,AllNotSupportive).getQuality === NXQuality.SILVER);
	assert(APEQualityRule(Uncertain,AllUncertain,AllSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,AllUncertain,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,AllUncertain,AllUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,AllUncertain,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(Uncertain,AllUncertain,AllNotSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllSupportive,AllSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllSupportive,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllSupportive,AllUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllSupportive,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllSupportive,AllNotSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,AllSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,AllUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,BestIsSupportive,AllNotSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllUncertain,AllSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllUncertain,BestIsSupportive).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllUncertain,AllUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllUncertain,BestIsUncertain).getQuality === NXQuality.BRONZE);
	assert(APEQualityRule(NotSupportive,AllUncertain,AllNotSupportive).getQuality === NXQuality.BRONZE);
    
    
  }

}