package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parsers.bed.commons.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.PrintWriter
import org.nextprot.parsers.bed.service.BEDAnnotationService
import org.nextprot.parsers.bed.service.OntologyService
import org.nextprot.parsers.bed.service.OntologyService

class OntologyServiceTest extends FlatSpec with Matchers {

  it should " on" in {
     println(OntologyService.getGoSubCategoryFromAccession("GO:0008219"));
  }

  it should " on new go terms" in {
     println(OntologyService.getGoSubCategoryFromAccession("GO:0099608"));
  }
  

}