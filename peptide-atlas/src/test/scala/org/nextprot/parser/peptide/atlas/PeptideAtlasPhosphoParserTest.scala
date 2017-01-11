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

    val peptides = parser.parse("src/test/resources/org/nextprot/parser/peptide/atlas/sample.tsv");
    //val peptides = parser.parse("/Users/agateau/Workspace-scala/nextprot-parsers/peptide-atlas/peptide_ptm_noSNP.tsv");
    
    assert(7 == parser.pep_count) // raw peptides as delivered in tsv by PeptideAtlas
    assert(7 == peptides.size) // peptide objects generated through parsing
  }

  it should "output proper DbXref XML" in {

    val dbref = new DbXref(_mData="MDATA_0066", _pmid="24400987", _quality=null)
    val xml = dbref.toXML
    
    assert("MDATA_0066" == (xml \ "document" \ "@id").text)
    assert("24400987" == (xml \ "@id").text)
  }
  
  it should "output proper Feature XML" in {

    val xml = feature1.toXML
    
    assert("modified residue" == (xml \ "@type").text)
    assert("phosphoserine" == (xml \ "@description").text)
    assert("GOLD" == (xml \ "@quality").text)
    assert("7" == (xml \ "location" \ "position" \ "@position").text)
    assert(3 == ((xml \ "dbReference").map(dbrefelem => {})).size ) // number of dbReference elements
  }
  

  it should "output proper Peptide XML" in {

    val peptide = new Peptide(_sequence="RPGGEPSPEGTTGQSYNQYSQR", id="PAp00000083", _dbrefs=List(dbref1,dbref2), _features=List(feature1,feature2))
    val xml = peptide.toXML
    
    assert("RPGGEPSPEGTTGQSYNQYSQR" == (xml \ "@sequence").text)
    assert("PeptideAtlas human phosphoproteome" == (xml \ "evidence" \ "@assigned_by").text)
    assert(2 == ((xml \ "feature").map(feature => {})).size ) // number of feature elements
    val dbrefs = (xml \ "dbReference").map(dbrefelem => {dbrefelem}).toList// dbReference elements
    assert(3 == dbrefs.size)  // number of dbReference elements
    assert("PAp00000083" == (dbrefs.head \ "@id").text) // first dbReference element
  } 
}