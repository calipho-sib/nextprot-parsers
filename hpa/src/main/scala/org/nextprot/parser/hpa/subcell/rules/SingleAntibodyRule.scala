package org.nextprot.parser.hpa.subcell.rules

import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.constants.HPAValidationValue._
import org.nextprot.parser.core.stats.StatisticsCollector
import org.nextprot.parser.core.stats.Stats

case class AntibodyValidationRule(hpaPA: HPAValidationValue, hpaIF: HPAValidationValue, hpaWB: HPAValidationValue) {

  def getQuality: NXQuality =
    
  this match {
    case AntibodyValidationRule(Supportive, Supportive, Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-S-S => GOLD"); GOLD }
    case AntibodyValidationRule(Supportive, Supportive, Uncertain) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-S-U => SILVER"); SILVER }
    case AntibodyValidationRule(Supportive, Supportive, Not_Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-S-N => SILVER"); SILVER }
    case AntibodyValidationRule(Supportive, Uncertain, Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-U-S => SILVER"); SILVER }
    case AntibodyValidationRule(Supportive, Uncertain, Uncertain) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-U-U => SILVER"); SILVER }
    case AntibodyValidationRule(Supportive, Uncertain, Not_Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-U-N => SILVER"); SILVER }
    case AntibodyValidationRule(Supportive, Not_Supportive, Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-N-SU => BRONZE"); BRONZE }
    case AntibodyValidationRule(Supportive, Not_Supportive, Uncertain) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-N-U => BRONZE"); BRONZE }
    case AntibodyValidationRule(Supportive, Not_Supportive, Not_Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "S-N-N => BRONZE"); BRONZE }
    case AntibodyValidationRule(Uncertain, Supportive, Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-S-S => SILVER"); SILVER }
    case AntibodyValidationRule(Uncertain, Supportive, Uncertain) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-S-U => SILVER"); SILVER }
    case AntibodyValidationRule(Uncertain, Supportive, Not_Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-S-N => SILVER"); SILVER }
    case AntibodyValidationRule(Uncertain, Uncertain, Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-U-S => SILVER"); SILVER }
    case AntibodyValidationRule(Uncertain, Uncertain, Uncertain) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-U-U => BRONZE"); BRONZE }
    case AntibodyValidationRule(Uncertain, Uncertain, Not_Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-U-N => BRONZE"); BRONZE }
    case AntibodyValidationRule(Uncertain, Not_Supportive, Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-N-S => BRONZE"); BRONZE }
    case AntibodyValidationRule(Uncertain, Not_Supportive, Uncertain) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-N-U => BRONZE"); BRONZE }
    case AntibodyValidationRule(Uncertain, Not_Supportive, Not_Supportive) => { Stats ++ ("SINGLE-OR-SELECTED-RULE", "U-N-N => BRONZE"); BRONZE }
    case _ => throw new Exception("AntibodyValidationRule not found: " + this)

  }


}