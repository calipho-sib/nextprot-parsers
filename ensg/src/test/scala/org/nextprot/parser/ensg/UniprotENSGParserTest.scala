package org.nextprot.parser.ensg

import org.scalatest._

import org.scalatest.Matchers

import scala.xml.PrettyPrinter

class UniprotENSGParserTest extends FlatSpec with Matchers {

  val prettyPrinter = new PrettyPrinter(1000, 4)

  it should "extract all ENSG references successfully from a Uniprot file" in {

    val parser = new UniprotENSGNXParser()

    val output = parser.parse(getClass.getResource("A0AVT1.xml").getFile)

    val expected = "A0AVT1 ENSG00000033178 ENSG00000033178"

    Assertions.equals(expected, output)

    /**
     * Matched XML elements:
     *
     * <dbReference id="ENST00000322244" type="Ensembl">
     *   <molecule id="A0AVT1-1"/>
     *   <property type="protein sequence ID" value="ENSP00000313454"/>
     *   <property type="gene ID" value="ENSG00000033178"/>
     * </dbReference>
     * <dbReference id="ENST00000420827" type="Ensembl">
     *   <molecule id="A0AVT1-3"/>
     *   <property type="protein sequence ID" value="ENSP00000399234"/>
     *   <property type="gene ID" value="ENSG00000033178"/>
     * </dbReference>
     *
     * Expected output value:
     *
     * A0AVT1 ENSG00000033178 ENSG00000033178
     */
  }
}
