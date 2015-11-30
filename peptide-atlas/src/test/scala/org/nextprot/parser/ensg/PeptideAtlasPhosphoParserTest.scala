package org.nextprot.parser.peptide.atlas

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class PeptideAtlasPhosphoParserTest extends FlatSpec with Matchers {

  it should "extract all peptides from a sample" in {

    val parser = new PeptideAtlasPhosphoNXParser()

    //parser.parse(getClass.getResource("sample.tsv").getFile)

    /*assert("A0AVT1" == accession)
    assert("ENSG00000033178,ENSG00000033178" == ensgs)*/
  }

}