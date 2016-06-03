package org.nextprot.parser.bed

import java.io.File
import java.io.PrintWriter
import org.nextprot.commons.statements.MappedStatement
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.nextprot.parser.bed.service.BEDAnnotationService
import org.nextprot.commons.statements.RawStatement

class BEDGenerateEvidences extends FlatSpec with Matchers {

  val entryElem = scala.xml.XML.loadFile(new File("ln-s-data.xml"))

  it should "group annotations together by subject and object" in {

    val pw = new PrintWriter(new File("/Users/dteixeira/Documents/file.tsv"))

    val annotations = BEDAnnotationService.getBEDAnnotations(entryElem);
    val vpAnnotations = annotations.filter(a => a.isVP);
    val vpGoEvidences = vpAnnotations.flatMap(a => a._evidences).filter(e => (e.isVP && e.isGO));

    vpGoEvidences.foreach(vpgoe => {
      
      val vpStatement = new RawStatement();
      val normalStatement = new RawStatement();
 
      normalStatement.setAnnot_cv_term_accession(vpgoe._bedObjectCvTerm.accession)
      normalStatement.setAnnot_cv_term_accession(vpgoe._bedObjectCvTerm.accession)

      vpStatement.setAnnotation_category("impact-annotation");
      vpStatement.setAnnot_cv_term_accession(vpgoe.getRelationInfo.getImpact().name);
      vpStatement.setReference_annot_hash(normalStatement.getAnnot_hash());
      
      
    });
    
    pw.close();

  }

  def underscoreToCamel(name: String) = "_([a-z\\d])".r.replaceAllIn(name, { m =>
    m.group(1).toUpperCase()
  })
}