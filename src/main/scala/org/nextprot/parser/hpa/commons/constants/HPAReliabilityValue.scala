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
      case "supportive" => return Uncertain;
      case "supported" => return Uncertain; // Supposedely obsolete but still in a few records
      case "approved" => return Uncertain;
      case "uncertain" => return NotSupportive;
      case _ => throw new Exception(s + " not found for HPAReliabilityValue")
    }

  }

}
