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
      case "supportive" => return Supportive;
      case "uncertain" => return Uncertain;
      case "non-supportive" => return NotSupportive;
      case _ => throw new Exception(s + " not found for HPAReliabilityValue")
    }

  }

}
