package org.nextprot.parser.bed.commons

import scala.language.implicitConversions

object BEDImpact extends Enumeration {

  protected case class Val(name: String, accession: String) extends super.Val(nextId, name) {
  }
  
  /**
   * This class should probably changed by reading the OBO file now on github:
   * https://github.com/calipho-sib/controlled-vocabulary/blob/master/cv_modification_effect.obo
   * 
   */
  implicit def valueofModifiers(x: Value) = x.asInstanceOf[Val]

  val NO_IMPACT = Val("no impact", "ME:0000003")
  val IMPACT = Val("impact", "ME:0000002")
  val INCREASE = Val("increase", "ME:0000005")
  val DECREASE = Val("decrease", "ME:0000004")
  val GAIN = Val("gain", "ME:0000006")

  val DEPOLARIZE = Val("depolarize", "ME:0000007")
  val HYPERPOLARIZE = Val("hyperpolarize", "ME:0000008")
  val IMPACT_ON_TEMPERATURE_DEPENDENCE_OF = Val("impact on temperature-dependence of", "ME:0000009")
  val NO_IMPACT_ON_TEMPERATURE_DEPENDENCE_OF = Val("no impact on temperature-dependence of", "ME:0000010")
  val SLOW = Val("slow", "ME:0000011")
  val HASTEN = Val("hasten", "ME:0000012")
    
}
