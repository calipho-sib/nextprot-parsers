package org.nextprot.parser.hpa.subcell
import org.scalatest._
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._

abstract class HPASubcellTestBase extends FlatSpec with Matchers {
 
	val slSection = "subcellularLocation" // secton in HPA XML specific for subcellular location
  
    System.setProperty("hpa.mapping.file", "src/test/resources/HPA_Subcell_Mapping.txt")
    System.setProperty("hpa.anti.multi.file", "src/test/resources/multi_target_antibodies.txt")


}

