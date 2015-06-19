package org.nextprot.parser.ensg

import org.scalatest._

import org.scalatest.Matchers

import scala.xml.PrettyPrinter

class UniprotENSGParserTest extends FlatSpec with Matchers {

  it should "extract all ENSG references successfully from a Uniprot file" in {

    val parser = new UniprotENSGNXParser()

    val (accession: String, ensgs: String) = parser.parse(getClass.getResource("A0AVT1.xml").getFile)

    Assertions.equals("A0AVT1", accession)
    Assertions.equals("ENSG00000033178,ENSG00000033178", ensgs)
  }
}