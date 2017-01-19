package org.nextprot.parser.bed

import org.nextprot.commons.statements.StatementField
import org.nextprot.parser.bed.converter.BedStatementConverter
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class BedGenerateMappedStatements extends FlatSpec with Matchers {

  //cp /Volumes/common/Calipho/navmutpredict/xml/*.xml ~/Documents/bed/
  //cp /Volumes/common/Calipho/caviar/xml/*.xml ~/Documents/bed/
  BedStatementConverter.addProxyDir("/Users/dteixeira/Documents/nxflat-proxy/");
  val statements = BedStatementConverter.convert("bioeditor", "2017-01-13", "brca1")._1;

  it should "return more than 1000 statements for brca1" in {

    assert(statements.length > 1000)

    val noImpactStatements = statements.filter { s => "no impact".equals(s.getValue(StatementField.ANNOT_CV_TERM_NAME)); }.toList
    assert(statements.length > 100)

    println(noImpactStatements(1).getValue(StatementField.ANNOT_DESCRIPTION));
    assert(noImpactStatements(1).getValue(StatementField.ANNOT_DESCRIPTION).startsWith("has no impact on"));

  }
  
  
   it should "return description with binding with the gene name" in {

    assert(statements.length > 1000)

    val impactsOnBinding = statements.filter (s => if(s.getValue(StatementField.ANNOT_DESCRIPTION) != null) s.getValue(StatementField.ANNOT_DESCRIPTION).contains("binding to") else false)
    .map(_.getValue(StatementField.ANNOT_DESCRIPTION)).toList

    //The description should not contain the accession, but the gene name instead
    assert(impactsOnBinding.filter(_.contains("NX_")).size === 0)
    
  }

  it should "generate correctly the description" in {

    assert(statements.length > 1000)

    val binaryInteraction = statements.filter { s => "increases binding to".equals(s.getValue(StatementField.ANNOT_DESCRIPTION)); }.toList
    assert(statements.length > 10)

    val noImpactStatements = statements.filter { s => "no impact".equals(s.getValue(StatementField.ANNOT_CV_TERM_NAME)); }.toList
    assert(statements.length > 10)
    assert(noImpactStatements(0).getValue(StatementField.ANNOT_DESCRIPTION).startsWith("has no impact on"));

  }

  it should "be gold for all variants and mutagenesis" in {

    val variantStatements = statements.filter(_.getValue(StatementField.ANNOTATION_CATEGORY).equals("variant")).toList
    assert(variantStatements.length > 10)

    val distinctQualities = variantStatements.map(_.getValue(StatementField.EVIDENCE_QUALITY)).toList.distinct;
    assert(distinctQualities.length.equals(1));
    assert(distinctQualities(0).equals("GOLD"));
    
  }

    it should "retrieve binary interactions only with neXtProt entries" in {
      val ss = statements.filter( _.getValue(StatementField.ANNOTATION_CATEGORY) == "binary-interaction").map(_.getValue(StatementField.BIOLOGICAL_OBJECT_ACCESSION)).distinct;
      assert(!ss.isEmpty);
      ss.foreach { s => 
        assert(s.startsWith("NX_"))
      };
  }
  

}