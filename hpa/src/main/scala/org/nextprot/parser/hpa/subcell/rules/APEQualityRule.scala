package org.nextprot.parser.hpa.subcell.rules

import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue
import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue._
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._
import scala.collection.mutable.Map
import org.nextprot.parser.hpa.constants.HPAAPEReliabilityValue._

case class APEQualityRule(reliability: HPAAPEReliabilityValue, hpaPA: HPAAPEValidationValue, hpaWB: HPAAPEValidationValue) {

  /** 
   *  To be validated with Paula
   */
  def getQuality: NXQuality = {
    val res: NXQuality =
     APEQualityRule.this match {
      case APEQualityRule(Not_supportive, _, _) => BRONZE
      case APEQualityRule(Uncertain, Not_Supportive, _) => BRONZE              // check with Paula
      case APEQualityRule(Uncertain, UncertainOne, Not_Supportive) => BRONZE
      case APEQualityRule(Uncertain, UncertainOne, UncertainOne) => BRONZE
      case APEQualityRule(Uncertain, UncertainOne, UncertainAll) => BRONZE     // check with Paula
      case APEQualityRule(Uncertain, UncertainOne, SupportiveOne) => BRONZE
      case APEQualityRule(Supportive, SupportiveOne, SupportiveOne) => GOLD
      case APEQualityRule(Supportive, SupportiveOne, SupportiveAll) => GOLD
      case APEQualityRule(Supportive, SupportiveAll, SupportiveOne) => GOLD
      case APEQualityRule(Supportive, SupportiveAll, SupportiveAll) => GOLD
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