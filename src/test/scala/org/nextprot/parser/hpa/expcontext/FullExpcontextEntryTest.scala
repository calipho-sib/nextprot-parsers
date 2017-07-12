package org.nextprot.parser.hpa.expcontext

import org.scalatest._
import org.nextprot.parser.core.NXParser
import java.io.File
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue
import scala.util.matching.Regex
import scala.xml.PrettyPrinter
import java.io.FileWriter
import org.nextprot.parser.core.datamodel.TemplateModel

class FullExpcontextEntryTest extends HPAExpcontextTestBase {

  "The HPAExpcontextNXParser " should " parse successfully a whole HPA file" in {

    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFile);
    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272047.xml";
    try {
      val result = hpaParser.parse(fname);
      result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
      val model = accumulator.getTemplateModel();
      assert(true)
    } catch {
      case e: Exception => {
        assert(false)
      }
    }
  }

  // see also properties set in HPAExpcontextTestBase for "hpa.tissue.mapping.file" = hpa - caloha mappping file
  "The HPAExpcontextNXParser " should " contains 0 comments about problems with the latest hpa-caloha mapping file" in {

    // this mapping file is supposed to be up to date (see also default for this property in parent class
    System.setProperty("hpa.tissue.mapping.file", "src/test/resources/NextProt_tissues.from-db.txt")

    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272333.xml";
    val result = hpaParser.parse(fname);

    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFile);
    result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
    val model = accumulator.getTemplateModel();

    val list = model.toXML.child.filter(_.isInstanceOf[scala.xml.Comment]).map(x => x.toString)
    if (list.size == 0) {
      assert(true)
    } else {
      println(fname)
      list.foreach(println(_))
      assert(false)
    }
  }

  "The HPAExpcontextNXParser " should " contains 4 comments about inconsistencies in hpa-caloha mapping file" in {

    // old version of mapping file causing problems
    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272047.xml";
    val result = hpaParser.parse(fname);
    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.with2warnings.txt")));
    result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
    val model = accumulator.getTemplateModel();

//    val pp = new PrettyPrinter(1000,4)
//    val str = pp.format(model.toXML).substring(0,800)
//    println(str)
    
    assert(
      model.toXML.child.
        filter(_.isInstanceOf[scala.xml.Comment]).
        filter(_.toString.startsWith("<!--Multiple lines for synonym")).
        size == 4)
  }

  // see also properties set in HPAExpcontextTestBase for "hpa.tissue.mapping.file" = hpa - caloha mappping file
  "The HPAExpcontextNXParser " should " contains 3 comments about hpa terms not found in hpa-mapping file" in {

    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272047.xml";
    val result = hpaParser.parse(fname);
    //println(wrapper.toXML.toString)

    // old version of mapping file causing problems
    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.with2warnings.txt")));
    result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
    val model = accumulator.getTemplateModel();

    assert(
      model.toXML.child.
        filter(_.isInstanceOf[scala.xml.Comment]).
        filter(_.toString.startsWith("<!--Mapping not found")).
        size == 3)
  }

  "The HPAExpcontextNXParser " should " contains produce exactly this output for 4 tissues" in {

    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272333.xml";
    val result = hpaParser.parse(fname);

    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.txt")));
    result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
    val model = accumulator.getTemplateModel();

    val prettyPrinter = new PrettyPrinter(1000, 4)
    val output = prettyPrinter.format(model.toXML).replaceAll("[\n\r\t ]", "")
    // val expect = scala.xml.XML.loadFile("src/test/resources/ExpectedOutputForENSG00000272333.xml")..replaceAll("[\n\r\t ]", "")
    val expect = scala.io.Source.fromFile("src/test/resources/ExpectedOutputForENSG00000272333.xml", "utf-8").getLines.mkString.replaceAll("[\n\r\t ]", "")
    //     println("---------------------------------------")
    //     println(output)
    //     println("---------------------------------------")
    //     println("---------------------------------------")
    //     println(expect)
    //     println("---------------------------------------")
    //	   println("output length:"+output.length())
    //	   println("expect length:"+expect.length())
    assert(output == expect)

  }

  "The HPAExpcontextNXParser " should " parse a HPA file and throw an error on assay type cancer" in {

    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272104.xml";
    try {

      val result = hpaParser.parse(fname);
      val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.txt")));
      result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
      val model = accumulator.getTemplateModel();

    } catch {
      case e: NXException => {
        if (e.getNXExceptionType == CASE_ASSAY_TYPE_NOT_TISSUE) {
          assert(true)
        } else {
          println("Unexpected NXException error case: " + e.getNXExceptionType.toString())
          assert(false)
        }
      }
      case e: Exception => {
        println("Unexpected Exception")
        e.printStackTrace()
        assert(false)
      }
    }
  }

  "The HPAExpcontextNXParser " should " parse a subset of HPA files and resulting XML should contain no WARNING comments" in {

    assert(loopOnFiles(true, checkNoWarningComment))

  }

  def checkNoWarningComment(template: TemplateModel): Boolean = {
    return (template.toXML.child.
      filter(_.isInstanceOf[scala.xml.Comment]).
      size == 0)
  }


}
  
  

