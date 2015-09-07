package org.nextprot.parser.ensg

import org.scalatest._

import org.scalatest.Matchers

class UniprotENSGParserTest extends FlatSpec with Matchers {

  it should "extract all ENSG references successfully from a Uniprot file" in {

    val parser = new UniprotENSGNXParser()

    val (accession: String, ensgs: String) = parser.parse(getClass.getResource("A0AVT1.xml").getFile)

    assert("A0AVT1" == accession)
    assert("ENSG00000033178,ENSG00000033178" == ensgs)
  }

  it should "extract all ENSG references successfully from Uniprot entry file P0DMU8" in {

    val parser = new UniprotENSGNXParser()

    val (accession: String, ensgs: String) = parser.parse(getClass.getResource("P0DMU8.xml").getFile)

    assert("P0DMU8" == accession)
    assert("ENSG00000228836,ENSG00000228836" == ensgs)
  }

  it should "extract all ENSG references successfully from Uniprot entry file P0DMW4" in {

    val parser = new UniprotENSGNXParser()

    val (accession: String, ensgs: String) = parser.parse(getClass.getResource("P0DMW4.xml").getFile)

    assert("P0DMW4" == accession)
    assert("" == ensgs)
  }
}