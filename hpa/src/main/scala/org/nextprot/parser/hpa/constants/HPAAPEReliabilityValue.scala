package org.nextprot.parser.hpa.constants

/**
 * Reliability value
 */
object HPAAPEReliabilityValue extends Enumeration {
  type HPAAPEReliabilityValue = Value
  val Supportive, Uncertain, NotSupportive = Value

  /**
   * Transforms value of XML to object enumeration
   */
  final def withName(s: String)(implicit dummy: DummyImplicit): HPAAPEReliabilityValue = {

    s match {
      case "supportive" => return Supportive;
      case "uncertain" => return Uncertain;
      case "non-supportive" => return NotSupportive;
      case _ => throw new Exception(s + " not found for HPAAPEReliabilityValue")
    }

  }

}
