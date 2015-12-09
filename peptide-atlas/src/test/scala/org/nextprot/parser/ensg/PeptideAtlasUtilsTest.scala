package org.nextprot.parser.peptide.atlas

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class PeptideAtlasUtilsTest extends FlatSpec with Matchers {

  it should "return proper mod_res" in {

    val modseq = "n[145]RLS[167]EDYGVLK[272]"
    assert("phosphoserine" == PeptideAtlasUtils.getMod(modseq, 3))
  }
}
