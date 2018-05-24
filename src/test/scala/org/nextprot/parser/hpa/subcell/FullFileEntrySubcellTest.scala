package org.nextprot.parser.hpa.subcell

import org.scalatest._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import scala.xml.PrettyPrinter
import scala.xml.XML
import org.nextprot.parser.hpa.utils.XMLComparator
import java.io.File
import org.nextprot.parser.core.exception.NXException

class FullFileEntrySubcellTest extends HPASubcellTestBase {

  val prettyPrinter = new PrettyPrinter(1000, 4);
  val testFolder = "src/test/resources/hpa/subcell/";

  "The HPASubcellNXParser " should " parse successfully a whole HPA file" in {

    val hpaParser = new HPASubcellNXParser();

    val tm = hpaParser.parse(testFolder + "subcell-file-input.xml");
    val output = tm.toXML;
    assert(XMLComparator.compareXMLWithFile(output, new File(testFolder + "subcell-expected-output-test.xml")))

  }

   
    "The HPASubcellNXParser " should " process generate an XML with element annotationTag set to SubCell" in {

    val fname = "src/test/resources/hpa/subcell/subcell-file-input.xml"
    val parser = new HPASubcellNXParser();
    val template = parser.parse(fname);
    println(template.toXML.toString.substring(0,600))
    val data = (template.toXML \ "annotationTag").text.trim()
    assert("SubCell".equals(data))
  }
  


  
  
}
