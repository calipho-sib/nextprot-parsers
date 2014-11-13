package org.nextprot.parser.hpa.subcell.constants

object HPAAPEReliabilityValue extends Enumeration {
  type HPAAPEReliabilityValue = Value
  val High, Low, Medium, Very_Low = Value

  final def withName(s: String)(implicit dummy: DummyImplicit): HPAAPEReliabilityValue = {

    s match {
      case "high" => return High;
      case "low" => return Low;
      case "medium" => return Medium;
      case "very low" => return Very_Low;
      case _ => throw new Exception(s + " not found for HPAAPEReliabilityValue")
    }

  }

}


object HPAAPEReliabilityValue2014 extends Enumeration {
  type HPAAPEReliabilityValue2014 = Value
  val Supportive, Uncertain, Not_supportive = Value
  final def withName(s: String)(implicit dummy: DummyImplicit): HPAAPEReliabilityValue2014 = {
    s match {
      case "high" => return Supportive;
      case "low" => return Uncertain;
      case "medium" => return Supportive;
      case "very low" => return Not_supportive;
      case _ => throw new Exception(s + " not found for HPAAPEReliabilityValue2014")
    }

  }

}
