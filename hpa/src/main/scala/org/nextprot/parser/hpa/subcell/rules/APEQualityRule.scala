package org.nextprot.parser.hpa.subcell.rules

import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue._
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue._
import scala.collection.mutable.HashTable
import java.util.Hashtable
import org.nextprot.parser.core.stats.StatisticsCollector
import org.nextprot.parser.core.stats.StatisticsCollectorSingleton


case class APEQualityRule(reliability: HPAAPEReliabilityValue, hpaPA: HPAValidationValue) {

  def getQuality: NXQuality =
    APEQualityRule.this match {
      case APEQualityRule(High, _) => {
        StatisticsCollectorSingleton.increment("APEQualityRule", "High, _");
        GOLD
      }
      case APEQualityRule(Medium, Supportive) => {
        StatisticsCollectorSingleton.increment("APEQualityRule", "Medium, Supportive");
        SILVER
      }
      case APEQualityRule(Medium, Uncertain) => {
        StatisticsCollectorSingleton.increment("APEQualityRule", "Medium, Uncertain");
        SILVER
      }
      case APEQualityRule(Low, Supportive) => {
        StatisticsCollectorSingleton.increment("APEQualityRule", "Low, Supportive");
        SILVER
      }
      case APEQualityRule(Low, Uncertain) => {
        StatisticsCollectorSingleton.increment("APEQualityRule", "Low, Uncertain");
        BRONZE
      }
      case APEQualityRule(Very_Low, _) => {
        StatisticsCollectorSingleton.increment("APEQualityRule", "Very_Low, _");
        BRONZE
      }
      case _ => throw new Exception("APEQualityRule not found: " + this)
    }

}

