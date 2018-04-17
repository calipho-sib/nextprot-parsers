package org.nextprot.parser.hpa.commons.constants

/**
 * Reliability value
 */
object HPAReliabilityValue extends Enumeration {
  type HPAReliabilityValue = Value
  val Supportive, Uncertain, NotSupportive = Value

  /**
   * Transforms value of XML to object enumeration
   */
  final def withName(s: String)(implicit dummy: DummyImplicit): HPAReliabilityValue = {

    s match {
      case "enhanced" => return Supportive;
      case "supportive" => return Supportive;
      case "supported" => return Supportive; // Supposedely obsolete but still in a few records
      case "uncertain" => return Uncertain;
      case "approved" => return Uncertain;
      case _ => throw new Exception(s + " not found for HPAReliabilityValue")
    }

  }

}
