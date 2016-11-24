package org.nextprot.parser.peptide.atlas

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.collection.mutable.ArrayBuffer


class PeptideAtlasUtilsTest extends FlatSpec with Matchers {

    val peplines = ArrayBuffer("RPGGEPSPEGTTGQSYNQYSQR  RPGGEPSPEGTTGQSYNQYS[167]QR 20  1.000 5304- P02751",
        "RPGGEPSPEGTTGQSYNQYSQR RPGGEPSPEGTTGQSYNQYS[167]QR 20  0.993 5305- P02751",
        "RPGGEPSPEGTTGQSYNQYSQR RPGGEPS[167]PEGTTGQSYNQYSQR 7 1.000 5305- P02751")

  it should "return proper mod_res" in {

    val modseq = "n[145]RLS[167]EDYGVLK[272]"
    assert("phosphoserine" == PeptideAtlasUtils.getMod(modseq, 3))
  }

  it should "return proper modified positions count" in {

    val poslist = PeptideAtlasUtils.getDistinctModPos(peplines)
    assert(2 == poslist.size)
  }
  
  it should "return proper modification for a feature" in {

    val mod = PeptideAtlasUtils.getModresForFeature(peplines,20)
    assert("phosphoserine" == mod)
  }
  
  it should "return proper map for sample ids with maping file 1" in {

    val sampleIdMap = PeptideAtlasUtils.getMetadataMap1("src/test/resources/org/nextprot/parser/peptide/atlas/Metadata_phosphosetPA.tsv") 
    assert(177 == sampleIdMap.size)
    assert("MDATA_0079-22496350" == sampleIdMap("5333"))
  }
  
  it should "return proper map for sample ids with maping file 2" in {

    val sampleIdMap = PeptideAtlasUtils.getMetadataMap("src/test/resources/org/nextprot/parser/peptide/atlas/metadata.txt") 
    assert(177 == sampleIdMap.size)
    assert("MDATA_0079-22496350" == sampleIdMap("5333"))
  }
  
}
