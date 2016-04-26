package org.nextprot.parser.bed

import java.io.File
import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class BedUtilsTest extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("/Users/dteixeira/beddata/data.xml"))

  it should "return a valid entry" in {
    val entryName = (entryElem \ "@accession").text;
    val variants = BEDUtils.getBEDVariants(entryElem);
    assert(entryName == "NX_P51587");

  }

  it should "return a list of variants" in {

    val entryName = (entryElem \ "@accession").text;
    val bedVariants = BEDUtils.getBEDVariants(entryElem);
    println(bedVariants.size);

  }

  it should "return a list of annotations" in {

    val entryName = (entryElem \ "@accession").text;
    val bedAnnotations = BEDUtils.getBEDAnnotations(entryElem);
    println(bedAnnotations.size);
    

  }
  
   it should "group annotations together by subject and relation" in {
     val annotations = BEDUtils.getBEDAnnotations(entryElem);
     println(annotations.groupBy(a => (a._subject, a.getAbsoluteObject)));
  }

}