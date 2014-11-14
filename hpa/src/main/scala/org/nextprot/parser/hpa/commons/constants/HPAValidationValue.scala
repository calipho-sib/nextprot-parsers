package org.nextprot.parser.hpa.commons.constants

/**
 * Used for WesterBlot and Protein Array values
 */
object HPAValidationValue extends Enumeration {

  type HPAValidationValue = Value
  val Supportive, Not_Supportive, Uncertain = Value
  
  /**
   * Transforms value of XML to object enumeration
   */
  final def withName(s: String)( implicit dummy: DummyImplicit ): HPAValidationValue = {
    
    s match {
      case "uncertain" => return Uncertain;
      case "supportive" => return Supportive;
      case "non-supportive" => return Not_Supportive;
      case _ => throw new Exception(s + " not found for HPAValidationValue")
    }
    
  }

}

