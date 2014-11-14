package org.nextprot.parser.hpa.expression

import org.scalatest._
import scala.xml.PrettyPrinter
import scala.xml.XML
import org.nextprot.parser.hpa.utils.XMLComparator
import java.io.File
import org.nextprot.parser.core.exception.NXException


class FullFileEntryExpressionTest extends HPAExpressionTestBase {

  val prettyPrinter = new PrettyPrinter(1000, 4);
  val testFolder = "src/test/resources/hpa/expression/";

  "The HPAExpressionNXParser " should " parse successfully a whole HPA file and create the expected output" in {

    val hpaParser = new HPAExpressionNXParser();

    val tm = hpaParser.parse(testFolder + "ENSG00000063177.xml");
    val output = tm.toXML;
    //println(output);
    
    assert(XMLComparator.compareXMLWithFile(output, new File(testFolder + "ExpectedOutputForENSG00000063177.xml")))

  }

}
