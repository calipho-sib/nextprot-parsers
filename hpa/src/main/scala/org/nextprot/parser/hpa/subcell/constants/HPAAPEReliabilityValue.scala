package org.nextprot.parser.hpa.subcell.constants

object HPAAPEReliabilityValue extends Enumeration {
  type HPAAPEReliabilityValue = Value
  val Supportive, Uncertain, Not_supportive = Value

  final def withName(s: String)(implicit dummy: DummyImplicit): HPAAPEReliabilityValue = {

    s match {
      case "supportive" => return Supportive;
      case "uncertain" => return Uncertain;
      case "non-supportive" => return Not_supportive;
      case _ => throw new Exception(s + " not found for HPAAPEReliabilityValue")
    }

  }

}
