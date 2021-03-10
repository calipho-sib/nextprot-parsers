package org.nextprot.parser.hpa.expression

import org.scalatest._
import java.io.FileWriter

import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._

import scala.xml.PrettyPrinter

class FullExpressionEntryTest extends HPAExpressionTestBase {

  System.setProperty("hpa.multiENSG.file", "src/test/resources/multiENSG_for_same_entry.txt")
  
  "The HPAExpressionNXParser " should " process successfully a file with zero uniprot ids" in {

    // uniprot ids are assigned later by the loader from the ENSG identifier
    val fname = "src/test/resources/ENSG-test-zero-uniprot-ids.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    //println(template.toXML.toString.substring(0,300))
    val data = (template.toXML \ "uniprotIds").text.trim()
    assert(data.length == 0)
  }

  "The HPAExpressionNXParser " should " process successfully a file with multiple uniprot ids" in {

    val fname = "src/test/resources/ENSG-test-multiple-uniprot-ids.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    //println(template.toXML.toString.substring(0,300))
    val data = (template.toXML \ "uniprotIds" \ "string").map(s => s.text).mkString(",")
    assert(data == "O43657,Q9NTW7")
  }

  "The HPAExpressionNXParser " should " discard file where no antibody has a tissueExpression with assayType tissue" in {

    val fname = "src/test/resources/ENSG-test-no-antibody-for-expr.xml"
    try {
      new HPAExpressionNXParser().parse(fname);
      assert(false)
    } catch {
      case e: NXException => {
        assert(e.getNXExceptionType == CASE_NO_ANTIBODY_FOUND_FOR_EXPR)
      }
    }
  }

  "The HPAExpressionNXParser " should " not discard file of type selected where one antibody have a tissueExpression with assayType tissue" in {

    val fname = "src/test/resources/ENSG-test-selected-with-non-ambiguous-antibody.xml"
    try {
      new HPAExpressionNXParser().parse(fname);
      assert(true)
    } catch {
      case e: NXException => {
        println(e.getNXExceptionType.description + " - " + e.getMessage)
        assert(false)
        //assert(e.getNXExceptionType == CASE_MORE_THAN_ONE_ANTIBODY_FOUND_FOR_SELECTED)
      }
    }
  }
 
  "The HPAExpressionNXParser " should " process successfully a file with single antibody" in {

    val fname = "src/test/resources/ENSG-test-expr-single.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    val pp = new PrettyPrinter(1000, 4)
    val output = pp.format(template.toXML)
    val fw = new FileWriter("ENSG-test-expr-single.output.xml", false)
    fw.write(output)
    fw.close
  }

  /*
  This test is ignored because this case (selected antibody) no longer exists according to the xsd of HPA
  See https://www.proteinatlas.org/download/proteinatlas.xsd
   */
  ignore should " process successfully a file with selected antibody" in {

    val fname = "src/test/resources/ENSG-test-expr-selected.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    val pp = new PrettyPrinter(1000, 4)
    val output = pp.format(template.toXML)
    val fw = new FileWriter("ENSG-test-expr-selected.output.xml", false)
    fw.write(output)
    fw.close
  }

  "The HPAExpressionNXParser " should " process successfully a file with multiple antibodies (APE)" in {

    val fname = "src/test/resources/ENSG-test-expr-ape.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    val pp = new PrettyPrinter(1000, 4)
    val output = pp.format(template.toXML)
    val fw = new FileWriter("ENSG-test-expr-ape.output.xml", false)
    fw.write(output)
    fw.close
  }

  "The HPAExpressionNXParser " should " generate a negative evidence when the tissue expression level is not detected, positive otherwise" in {

    val fname = "src/test/resources/ENSG-test-pos-neg-evidence.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    val evi = (template.toXML \ "expressionAnnotations" \\ "com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssoc").toList
    val res1 = (evi.filter(ev => (ev \\ "propertyValue").text == "medium") \ "isNegativeEvidence").text
    val res2 = (evi.filter(ev => (ev \\ "propertyValue").text == "not detected") \ "isNegativeEvidence").text
    assert(res1 == "false")
    assert(res2 == "true")
  }

  
  "The HPAExpressionNXParser " should " build a correct identifier for summary and tissue expression evidences when tissue is a simple word" in {

    val fname = "src/test/resources/ENSG-test-evidence-identifier.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    val summAc = (template.toXML \ "summaryAnnotations" \\ "com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssoc" \\ "accession").text
    assert(summAc == "ENSG00000113361/tissue")
    val exprAc = (template.toXML \ "expressionAnnotations" \\ "com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssoc" \\ "accession")
    exprAc.foreach(el => assert(el.text == "ENSG00000113361/tissue/endometrium 1"
                             || el.text == "ENSG00000113361/tissue/endometrium 2"))
    
  }

  "The HPAExpressionNXParser " should " build a correct identifier for summary and tissue expression evidences when tissue contains punctuation and multiple words" in {

    val fname = "src/test/resources/ENSG-test-evidence-identifier-2.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    val summAc = (template.toXML \ "summaryAnnotations" \\ "com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssoc" \\ "accession").text
    assert(summAc == "ENSG00000007376/tissue")
    val exprAc = (template.toXML \ "expressionAnnotations" \\ "com.genebio.nextprot.datamodel.annotation.AnnotationResourceAssoc" \\ "accession")
    exprAc.foreach(el => assert(el.text == "ENSG00000007376/tissue/cervix, uterine"))
    
  }
  
    "The HPAExpressionNXParser " should " process generate an XML with element annotationTag set to IHC" in {

    val fname = "src/test/resources/ENSG-test-with-ihc-and-rnaseq.xml"
    val parser = new HPAExpressionNXParser();
    val template = parser.parse(fname);
    //println(template.toXML.toString.substring(0,600))
    val data = (template.toXML \ "annotationTag").text.trim()
    assert("IHC".equals(data))
  }

  "The HPARNAExpressionNXParser " should " process generate an XML with element annotationTag set to RNASeq" in {

    val fname = "src/test/resources/ENSG-test-with-ihc-and-rnaseq.xml"
    val parser = new HPARNAExpressionNXParser();
    val template = parser.parse(fname);
    //println(template.toXML.toString.substring(0,600))
    val data = (template.toXML \ "annotationTag").text.trim()
    assert("RNASeq".equals(data))
  }

}
