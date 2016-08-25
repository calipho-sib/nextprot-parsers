package org.nextprot.parser.bed

import org.nextprot.commons.statements.StatementField
import org.nextprot.parsers.bed.converter.BedServiceStatementConverter
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import java.io.File
import org.nextprot.commons.statements.Statement

class BedAbleToParseAllGenes extends FlatSpec with Matchers {

 /*
  it should "not conflict statement ids" in {

    val buf = scala.collection.mutable.ArrayBuffer.empty[Statement]
    
    BedServiceStatementConverter.addProxyDir("/Users/dteixeira/Documents/bed/");
    val files = getListOfFiles(new File("/Users/dteixeira/Documents/bed/"));
    files.filter {f => !(f.getName().startsWith(".")) }.foreach {f => {
      val geneName = f.getName.replaceAll(".xml", "")
          buf ++= BedServiceStatementConverter.convert(geneName);
      }
    }
    
    println(buf.size);
    //println(buf.filter { s => s.getValue(StatementField.ANNOTATION_CATEGORY) == "variant"  && s.getValue(StatementField.REFERENCE_PUBMED) == "TOO_MANY_PUBLICATIONS"}.size);
    val variantsWithPubNotFoudn = buf.filter { s => s.getValue(StatementField.ANNOTATION_CATEGORY) == "variant"};//  && s.getValue(StatementField.REFERENCE_PUBMED) == "PUBLICATION_NOT_FOUND"};
    //println(variantsWithPubNotFoudn.size)
    
  }*/
  
  
  def getListOfFiles(dir: File):List[File] =
  dir.listFiles.filter(_.isFile).toList
  

  

}