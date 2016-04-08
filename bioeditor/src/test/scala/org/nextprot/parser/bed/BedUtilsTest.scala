package org.nextprot.parser.bed

import org.nextprot.parser.bed.utils.BEDUtils
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parser.core.exception.NXException

class BedUtilsTest extends FlatSpec with Matchers {

  it should "return a valid entry" in {

    val xml = <nxprotein accession="NX_P51587"/>;
    val entryName = BEDUtils.getBedAnnotations(xml);
    assert(entryName == "NX_P51587");

  }

}