package org.nextprot.parser.hpa.subcell.constants

object HPAValidationValue extends Enumeration {

  type HPAValidationValue = Value
  val Supportive, Not_Supportive, Uncertain = Value
  
  final def withName(s: String)( implicit dummy: DummyImplicit ): HPAValidationValue = {
    
    s match {
      case "uncertain" => return Uncertain;
      case "supportive" => return Supportive;
      case "non-supportive" => return Not_Supportive;
      case _ => throw new Exception(s + " not found for HPAValidationValue")
    }
    
  }

}

/**
 * For new rules 2014
 */
object HPAAPEValidationValue extends Enumeration {

  type HPAAPEValidationValue = Value
  val SupportiveAll, SupportiveOne, UncertainAll , UncertainOne , Not_Supportive = Value
  
  final def withName(s: String)( implicit dummy: DummyImplicit ): HPAAPEValidationValue = {
    
    s match {
      case "supportiveAll" => return SupportiveAll;
      case "supportiveOne" => return SupportiveOne;
      case "uncertainAll" => return UncertainAll;
      case "uncertainOne" => return UncertainOne;
      case "non-supportive" => return Not_Supportive;
      case _ => throw new Exception(s + " not found for HPAValidationValue")
    }
    
  }

}
