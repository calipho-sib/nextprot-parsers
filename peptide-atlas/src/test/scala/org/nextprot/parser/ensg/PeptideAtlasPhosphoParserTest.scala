package org.nextprot.parser.peptide.atlas

import org.scalatest.FlatSpec
import org.nextprot.parser.peptide.atlas.datamodel.Peptide
import org.nextprot.parser.peptide.atlas.datamodel.Feature
import org.nextprot.parser.peptide.atlas.datamodel.DbXref
import org.scalatest.Matchers

class PeptideAtlasPhosphoParserTest extends FlatSpec with Matchers {

    val dbref1 = new DbXref(_mData="MDATA_0066", _pmid="24400987", _quality=null)
    val dbref2 = new DbXref(_mData="MDATA_0070", _pmid=null, _quality=null)
    val dbref3 = new DbXref(_mData="MDATA_0066", _pmid="24400987", _quality="GOLD")
    val dbref4 = new DbXref(_mData="MDATA_0070", _pmid=null, _quality="SILVER")
    val dbref5 = new DbXref(_mData="MDATA_0101", _pmid="18272233", _quality="SILVER")
    val feature1 = new Feature(_position=7, _description="phosphoserine", _pepid="PAp00000083", _dbrefs=List(dbref3,dbref4))
    val feature2 = new Feature(_position=20, _description="phosphoserine", _pepid="PAp00000083", _dbrefs=List(dbref4))

  
  
  it should "extract all peptides from a sample" in {

    val parser = new PeptideAtlasPhosphoNXParser()

    //val xml = parser.parse("/home/agateau/workspace/nextprot-parsers/peptide-atlas/src/test/resources/org/nextprot/parser/peptide/atlas/sample.tsv")
    //parser.parse("/home/agateau/workspace/nextprot-parsers/peptide-atlas/src/test/resources/org/nextprot/parser/peptide/atlas/peptide_ptm_noSNP.tsv");
    parser.parse("/home/agateau/workspace/nextprot-parsers/peptide-atlas/src/test/resources/org/nextprot/parser/peptide/atlas/sample.tsv");
    
    assert(7 == parser.pep_count)
  }

  /*it should "output proper DbXref XML" in {

    val dbref = new DbXref(_mData="MDATA_0066", _pmid="24400987", _quality=null)

    println(dbref.toXML)
  }
  
  it should "output proper Feature XML" in {

    println(feature1.toXML)
  }
  

  it should "output proper Peptide XML" in {

    val peptide = new Peptide(_sequence="RPGGEPSPEGTTGQSYNQYSQR", id="PAp00000083", _dbrefs=List(dbref1,dbref2), _features=List(feature1,feature2))

    println(peptide.toXML)
  } */
}