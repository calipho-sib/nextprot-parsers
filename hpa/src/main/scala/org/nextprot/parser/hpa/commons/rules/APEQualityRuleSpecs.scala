
package org.nextprot.parser.hpa.commons.rules

import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue.HPAReliabilityValue
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.core.stats.Stats

/**
 * Rules as specified in the first document.
 * To see if they are ok
 * 
 */
case class APEQualityRuleSpecs(reliability: HPAReliabilityValue, pas: List[HPAValidationValue], wbs: List[HPAValidationValue]) {

  def getQuality: NXQuality = {

    //Reliability is Supportive
    if (reliability == HPAReliabilityValue.Supportive) {

      // 1 Supportive		   								supportive at least one AB	supportive at least one AB				Gold
      if (atLeastOneProteinArray(Supportive) && atLeastOneWesternBlot(Supportive)) {
        Stats ++ ("GoldRule", "RS aS aS")
        return GOLD;
      }

      // 2 Supportive		   								supportive at least one AB	uncertain all AB						Silver
      if (atLeastOneProteinArray(Supportive) && allWesternBlots(Uncertain)) {
        Stats ++ ("SilverRule", "RS aS AU")
        return SILVER;
      }

      //3 Supportive		   								supportive at least one AB	non-supportive all AB					Silver
      if (atLeastOneProteinArray(Supportive) && allWesternBlots(NotSupportive)) {
        Stats ++ ("SilverRule", "RS aS AN")
        return SILVER;
      }

      // 4 Supportive		  								 supportive at least one AB	uncertain and non-supportive ABs		Silver //TODO check if the 3rd case really means this
      if (atLeastOneProteinArray(Supportive) && (atLeastOneWesternBlot(Uncertain) && atLeastOneWesternBlot(NotSupportive))) {
        Stats ++ ("SilverRule", "RS aU (aU aN)")
        return SILVER;
      }

      // 5 Supportive		   								uncertain all AB			-											Silver
      if (allWesternBlots(Uncertain)) {
        Stats ++ ("SilverRule", "RS - AWU")
        return SILVER;
      }

    }

    //Reliability Uncertain
    if (reliability == HPAReliabilityValue.Uncertain) {

      // 6 Uncertain		   supportive all AB		    supportive all AB				    	Silver
      if (allProteinArrays(Supportive) && allWesternBlots(Supportive)) {
        Stats ++ ("SilverRule", "R6")
        return SILVER;
      }
      // 7 Uncertain		   supportive all AB		    uncert./non-supp. at least one AB		Silver
      if (allProteinArrays(Supportive) && (atLeastOneWesternBlot(Uncertain) && atLeastOneWesternBlot(NotSupportive))) {
        Stats ++ ("SilverRule", "R7")
        return SILVER;
      }
      //TODO I have added atLeastOneProteinArray(Supportive)
      // 8 Uncertain		   								uncertain at least one AB	       supportive all AB						Silver
      if ((atLeastOneProteinArray(Supportive) || atLeastOneProteinArray(Uncertain)) && allWesternBlots(Supportive)) {
        Stats ++ ("SilverRule", "R8")
        return SILVER;
      }

      //TODO check the 3rd case
      // 9 Uncertain		   							  uncertain at least one AB				uncert./non-supp. at least one AB		Bronze
      if (atLeastOneProteinArray(Uncertain)) {
        Stats ++ ("BronzRule", "R9")
        return BRONZE;
      }

    }

    //Reliability Uncertain
    if (reliability == HPAReliabilityValue.NotSupportive) {

      //Reliability Non-supportive
      // 10 Non-supportive      supportive all AB		    supportive all AB						Bronze
      if (allProteinArrays(Supportive) && allWesternBlots(Supportive)) {
        Stats ++ ("BronzeRule", "R10")
        return BRONZE;
      }
      // 11 Non-supportive	   supportive all AB		    uncert./non-supp. at least one AB		Bronze
      if (allProteinArrays(Supportive) && (atLeastOneWesternBlot(Uncertain) && atLeastOneWesternBlot(NotSupportive))) {
        Stats ++ ("BronzeRule", "R11")
        return BRONZE;
      }
      // 12 Non-supportive	   uncertain at least one AB	supportive all AB						Bronze
      if (atLeastOneProteinArray(Uncertain) && allWesternBlots(Supportive)) {
        Stats ++ ("BronzeRule", "R12")
        return BRONZE;
      }

      // 13 Non-supportive	   uncertain at least one AB	uncert./non-supp. at least one AB		Bronze
      //TODO check the 3rd case
      if (atLeastOneProteinArray(Uncertain)) {
        Stats ++ ("BronzeRule", "R13")
        return BRONZE;
      }

    }

    Stats ++ ("RuleNotFound", "---")
    //etc ...
    throw new Exception("Quality rule not found: " + this)

  }

  def atLeastOneWesternBlot(value: HPAValidationValue): Boolean = {
    return !wbs.filter(_ == value).isEmpty;
  }

  def allWesternBlots(value: HPAValidationValue): Boolean = {
    return wbs.filter(_ == value).size.equals(wbs.size);
  }

  def atLeastOneProteinArray(value: HPAValidationValue): Boolean = {
    return !pas.filter(_ == value).isEmpty;
  }

  def allProteinArrays(value: HPAValidationValue): Boolean = {
    return pas.filter(_ == value).size.equals(pas.size);
  }

}