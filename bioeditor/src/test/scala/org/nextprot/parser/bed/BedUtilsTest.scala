package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parser.bed.service.BEDAnnotationService

class BedUtilsTest extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("/Users/dteixeira/beddata/data.xml"))


  it should "return a list of annotations" in {

    val entryName = (entryElem \ "@accession").text;
    val bedAnnotations = BEDAnnotationService.getBEDVPAnnotations(entryElem);
    println(bedAnnotations.size);
    

  }
  
   it should "group annotations together by subject and relation" in {
     val annotations = BEDAnnotationService.getBEDVPAnnotations(entryElem);
     println(annotations.groupBy(a => (a._subject, a.getAbsoluteObject)));
  }

}