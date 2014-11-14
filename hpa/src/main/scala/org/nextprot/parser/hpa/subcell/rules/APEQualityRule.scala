package org.nextprot.parser.hpa.subcell.rules

import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue
import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue._
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._
import scala.collection.mutable.Map
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue._

case class APEQualityRule(reliability: HPAAPEReliabilityValue, hpaPA: HPAAPEValidationValue, hpaWB: HPAAPEValidationValue) {

/* 
 * Specs quality rules for tissue expression / subcell.location - table for APE (and other cases since 11.2014), document version 12
 * 
 * HPA Reliability		HPA PA				       		WB						        	 NP
 * 
 1 Supportive		   supportive at least one AB	supportive at least one AB				Gold
 2 Supportive		   supportive at least one AB	uncertain all AB						Silver
 3 Supportive		   supportive at least one AB	non-supportive all AB					Silver
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
  /** 
   *  Validated with Paula 14.11.2014
   */
  def getQuality: NXQuality = {
    val res: NXQuality =
     APEQualityRule.this match {
      case APEQualityRule(Not_supportive, _, _) => BRONZE  						// case 10,11,12,13
      case APEQualityRule(Uncertain, Not_Supportive, _) => BRONZE               // case unspecified in specs but approved by Paula, mail 14.11.2014
      case APEQualityRule(Uncertain, UncertainOne, Not_Supportive) => BRONZE	// case 9 with WB=non supp.
      case APEQualityRule(Uncertain, UncertainOne, UncertainOne) => BRONZE		// case 9 with WB uncertain one
      case APEQualityRule(Uncertain, UncertainOne, UncertainAll) => BRONZE      // case unspecified in specs but approved by Paula, mail 14.11.2014
      case APEQualityRule(Uncertain, UncertainOne, SupportiveOne) => BRONZE		// case 9
      case APEQualityRule(Supportive, SupportiveOne, SupportiveOne) => GOLD     // case 1
      case APEQualityRule(Supportive, SupportiveOne, SupportiveAll) => GOLD     // case 1
      case APEQualityRule(Supportive, SupportiveAll, SupportiveOne) => GOLD     // case 1
      case APEQualityRule(Supportive, SupportiveAll, SupportiveAll) => GOLD     // case 1
      case _ => SILVER
    }
    return res
  }
  /** 
   *  To be validated with Paula if default method is not ok
   */
  def getQualityExplicit: NXQuality =

    // PA: never Non_Supportive !
    
    APEQualityRule.this match {
      case APEQualityRule(Not_supportive, _, _) => BRONZE
      case APEQualityRule(Uncertain, Not_Supportive, _) => BRONZE
      case APEQualityRule(Uncertain, UncertainOne, Not_Supportive) => BRONZE
      case APEQualityRule(Uncertain, UncertainOne, UncertainOne) => BRONZE // check with Paula:OK
      case APEQualityRule(Uncertain, UncertainOne, UncertainAll) => BRONZE // check with Paula:OK
      case APEQualityRule(Uncertain, UncertainOne, SupportiveOne) => BRONZE
      case APEQualityRule(Uncertain, UncertainOne, SupportiveAll) => SILVER
      case APEQualityRule(Uncertain, UncertainAll, _) => SILVER // check with Paula:OK (include in UncertainAll)
      case APEQualityRule(Uncertain, SupportiveOne, _) => SILVER // check with Paula:OK 
      case APEQualityRule(Uncertain, SupportiveAll, _) => SILVER

      case APEQualityRule(Supportive, Not_Supportive, _) => SILVER // does not exist
      case APEQualityRule(Supportive, UncertainOne, _) => SILVER // doesn't exist
      case APEQualityRule(Supportive, UncertainAll, _) => SILVER 

      case APEQualityRule(Supportive, SupportiveOne, Not_Supportive) => SILVER
      case APEQualityRule(Supportive, SupportiveOne, UncertainOne) => SILVER
      case APEQualityRule(Supportive, SupportiveOne, UncertainAll) => SILVER
      case APEQualityRule(Supportive, SupportiveOne, SupportiveOne) => GOLD
      case APEQualityRule(Supportive, SupportiveOne, SupportiveAll) => GOLD

      case APEQualityRule(Supportive, SupportiveAll, Not_Supportive) => SILVER
      case APEQualityRule(Supportive, SupportiveAll, UncertainOne) => SILVER
      case APEQualityRule(Supportive, SupportiveAll, UncertainAll) => SILVER
      case APEQualityRule(Supportive, SupportiveAll, SupportiveOne) => GOLD
      case APEQualityRule(Supportive, SupportiveAll, SupportiveAll) => GOLD

      case _ => throw new Exception("APEQualityRule not found: " + this)
    }

  
  
}