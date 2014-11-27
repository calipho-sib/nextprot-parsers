/*
package org.nextprot.parser.hpa.commons.rules

import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue.HPAReliabilityValue;
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._

case class APEQualityRule(reliability: HPAReliabilityValue, pas: List[HPAValidationValue], wbs: List[HPAValidationValue]) {
*/
  /* 
 * Specs quality rules for tissue expression / subcell.location - table for APE (and other cases since 11.2014), document version 12
 * 
 * HPA Reliability		HPA PA				       		WB						        	 NP
 * 
 4 Supportive		   supportive at least one AB	uncertain and non-supportive ABs		Silver
 5 Supportive		   uncertain all AB			-											Silver
 
HPA Reliability			HPA PA			        		WB						         	 NP

 6 Uncertain		   supportive all AB		    supportive all AB				    	Silver
 7 Uncertain		   supportive all AB		    uncert./non-supp. at least one AB		Silver
 8 Uncertain		   uncertain at least one AB	supportive all AB						Silver
 9 Uncertain		   uncertain at least one AB	uncert./non-supp. at least one AB		Bronze
 
HPA Reliability			HPA PA				           	WB									 NP

10 Non-supportive      supportive all AB		    supportive all AB						Bronze
11 Non-supportive	   supportive all AB		    uncert./non-supp. at least one AB		Bronze
12 Non-supportive	   uncertain at least one AB	supportive all AB						Bronze
13 Non-supportive	   uncertain at least one AB	uncert./non-supp. at least one AB		Bronze
 * 
 */
 /*
  def getQuality: NXQuality = {
    
    // 1 Supportive		   supportive at least one AB	supportive at least one AB				Gold
    if (reliabilitySupportive && atLeastOneProteinArray(Supportive) && atLeastOneWesternBlot(Supportive)) return GOLD;
    // 2 Supportive		   supportive at least one AB	uncertain all AB						Silver
    if (reliabilitySupportive && atLeastOneProteinArray(Supportive) && allWesternBlots(Uncertain)) return SILVER;
    //3 Supportive		   supportive at least one AB	non-supportive all AB					Silver
    if (reliabilitySupportive && atLeastOneProteinArray(Supportive) && allWesternBlots(NotSupportive)) return SILVER;
    
    //etc ...
    throw new Exception("Quality rule not found: " + this)

  }

  def reliabilitySupportive: Boolean = {
    return reliability == HPAReliabilityValue.Supportive;
  }

  def atLeastOneWesternBlot(value: HPAValidationValue): Boolean = {
    return !wbs.filter(_ == value).size.equals(wbs.size);
  }

  def allWesternBlots(value: HPAValidationValue): Boolean = {
    return wbs.filter(_ == value).size.equals(wbs.size);
  }

  def atLeastOneProteinArray(value: HPAValidationValue): Boolean = {
    return !pas.filter(_ == value).size.equals(pas.size);
  }

  def allProteinArrays(value: HPAValidationValue): Boolean = {
    return pas.filter(_ == value).size.equals(pas.size);
  }

}*/