package org.nextprot.parser.bed

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parsers.bed.service.GeneNameService

class GeneNameServiceTest extends FlatSpec with Matchers {

  it should "return correct accession for gene name" in {

    assert("NX_P01308" === GeneNameService.getNXAccessionForGeneName("INS"));

  }

}