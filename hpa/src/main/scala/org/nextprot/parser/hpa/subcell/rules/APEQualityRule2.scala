package org.nextprot.parser.hpa.subcell.rules

import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue
import org.nextprot.parser.hpa.subcell.constants.HPAAPEValidationValue._
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue2014
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue2014._
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.constants.NXQuality._
import scala.collection.mutable.Map

object APEQualityRule2014Stats {

  val stats = Map[String,Integer]()
    

  def reset = {
    stats.empty
  }

  def show() = {
    var total: Integer = 0
    println("APE rules statistics")
    stats.foreach(s => {
      val (t, c) = s; println(t + "|" + c)
      total += c
    })
    println("TOTAL : " + total)
  }

  def incStatCase(rule:APEQualityRule2014, res:NXQuality) = {
    val key = rule.toString + "|" + res.toString()
    val cnt: Integer = stats.getOrElse(key, 0) 
    stats.put(key, cnt + 1)
  }

}

case class APEQualityRule2014(reliability: HPAAPEReliabilityValue2014, hpaPA: HPAAPEValidationValue, hpaWB: HPAAPEValidationValue) {

  
  
  /** 
   *  To be validated with Paula
   */
  def getQuality: NXQuality = {
    val res: NXQuality =
     APEQualityRule2014.this match {
      case APEQualityRule2014(Not_supportive, _, _) => BRONZE
      case APEQualityRule2014(Uncertain, Not_Supportive, _) => BRONZE              // check with Paula
      case APEQualityRule2014(Uncertain, UncertainOne, Not_Supportive) => BRONZE
      case APEQualityRule2014(Uncertain, UncertainOne, UncertainOne) => BRONZE
      case APEQualityRule2014(Uncertain, UncertainOne, UncertainAll) => BRONZE     // check with Paula
      case APEQualityRule2014(Uncertain, UncertainOne, SupportiveOne) => BRONZE
      case APEQualityRule2014(Supportive, SupportiveOne, SupportiveOne) => GOLD
      case APEQualityRule2014(Supportive, SupportiveOne, SupportiveAll) => GOLD
      case APEQualityRule2014(Supportive, SupportiveAll, SupportiveOne) => GOLD
      case APEQualityRule2014(Supportive, SupportiveAll, SupportiveAll) => GOLD
      case _ => SILVER
    }
    APEQualityRule2014Stats.incStatCase(this, res)
    return res
  }
  /** 
   *  To be validated with Paula if default method is not ok
   */
  def getQualityExplicit: NXQuality =

    // PA: never Non_Supportive !
    
    APEQualityRule2014.this match {
      case APEQualityRule2014(Not_supportive, _, _) => BRONZE
      case APEQualityRule2014(Uncertain, Not_Supportive, _) => BRONZE
      case APEQualityRule2014(Uncertain, UncertainOne, Not_Supportive) => BRONZE
      case APEQualityRule2014(Uncertain, UncertainOne, UncertainOne) => BRONZE // check with Paula:OK
      case APEQualityRule2014(Uncertain, UncertainOne, UncertainAll) => BRONZE // check with Paula:OK
      case APEQualityRule2014(Uncertain, UncertainOne, SupportiveOne) => BRONZE
      case APEQualityRule2014(Uncertain, UncertainOne, SupportiveAll) => SILVER
      case APEQualityRule2014(Uncertain, UncertainAll, _) => SILVER // check with Paula:OK (include in UncertainAll)
      case APEQualityRule2014(Uncertain, SupportiveOne, _) => SILVER // check with Paula:OK 
      case APEQualityRule2014(Uncertain, SupportiveAll, _) => SILVER

      case APEQualityRule2014(Supportive, Not_Supportive, _) => SILVER // does not exist
      case APEQualityRule2014(Supportive, UncertainOne, _) => SILVER // doesn't exist
      case APEQualityRule2014(Supportive, UncertainAll, _) => SILVER 

      case APEQualityRule2014(Supportive, SupportiveOne, Not_Supportive) => SILVER
      case APEQualityRule2014(Supportive, SupportiveOne, UncertainOne) => SILVER
      case APEQualityRule2014(Supportive, SupportiveOne, UncertainAll) => SILVER
      case APEQualityRule2014(Supportive, SupportiveOne, SupportiveOne) => GOLD
      case APEQualityRule2014(Supportive, SupportiveOne, SupportiveAll) => GOLD

      case APEQualityRule2014(Supportive, SupportiveAll, Not_Supportive) => SILVER
      case APEQualityRule2014(Supportive, SupportiveAll, UncertainOne) => SILVER
      case APEQualityRule2014(Supportive, SupportiveAll, UncertainAll) => SILVER
      case APEQualityRule2014(Supportive, SupportiveAll, SupportiveOne) => GOLD
      case APEQualityRule2014(Supportive, SupportiveAll, SupportiveAll) => GOLD

      case _ => throw new Exception("APEQualityRule not found: " + this)
    }


}