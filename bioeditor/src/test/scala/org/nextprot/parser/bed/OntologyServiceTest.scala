package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.parser.bed.service.OntologyService

class OntologyServiceTest extends FlatSpec with Matchers {

  it should " on" in {
     println(OntologyService.getGoSubCategoryFromAccession("GO:0008219"));
  }

  it should " on new go terms" in {
     println(OntologyService.getGoSubCategoryFromAccession("GO:0099608"));
  }
  

}