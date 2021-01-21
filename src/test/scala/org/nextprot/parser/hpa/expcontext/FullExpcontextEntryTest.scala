package org.nextprot.parser.hpa.expcontext

import org.scalatest._
import org.nextprot.parser.core.NXParser
import java.io.File

import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.HPAUtils.dataToNotExclude
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue

import scala.util.matching.Regex
import scala.xml.{Elem, PrettyPrinter}
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

  "The HPAExpcontextNXParser " should " contains 4 comments about inconsistencies in TEST hpa-caloha mapping file" in {

    // NextProt_tissues.from-db.with2warnings.txt should contains two terms should be mapped to two different terms
    // Currently, the mapped terms to two different terms are:
    // Myelencephalon|Myelencephalon|TS-2365
    // Myelencephalon|Medulla oblongata|TS-0607
    // Kidney proximal tubule|Renal proximal tubule|TS-0509
    // Kidney proximal tubule|Renal proximal convoluted tubule|TS-2198
    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272047.xml";
    val result = hpaParser.parse(fname);
    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.with2warnings.txt")));
    result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
    val model = accumulator.getTemplateModel();

//    val pp = new PrettyPrinter(1000,4)
//    val str = pp.format(model.toXML);
//    println("model: " + str)

    assert(
      model.toXML.child.
        filter(_.isInstanceOf[scala.xml.Comment]).
        filter(_.toString.startsWith("<!--Multiple lines for synonym")).
        size == 4)
  }

  
  
  // see also properties set in HPAExpcontextTestBase for "hpa.tissue.mapping.file" = hpa - caloha mappping file
  "The HPAExpcontextNXParser " should " contain 1 comment about hpa terms not found in TEST hpa-caloha mapping file" in {

    // NextProt_tissues.from-db.with2warnings.txt is a copy of NextProt_tissues.from-db.txt
    // with one mapped term (which is in the xml file) is removed.
    // Currently, 'Olfactory region' is removed
    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272333.xml";
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
        size == 1)
  }


  "The HPAExpcontextNXParser " should " contains find 54 consensus tissues and 20 blood cells in RNA expression and produce corresponding ECs" in {

    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000272333.xml";

    val xmlin = scala.xml.XML.loadFile(fname)
    val (consensusExpectedCount: Int, bloodExpectedCount: Int, brainExpectedCount: Int, expectedCount: Int) = getExpectedCounts(xmlin)

    println("INPUT - count of tissue in RNA expression section: " + consensusExpectedCount + " consensus tissues, " +
      bloodExpectedCount + " blood cells and " + brainExpectedCount + " human brain tissues")  // 54 consensus tissues and 20 blood cells
    
    val result = hpaParser.parse(fname);
    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.txt")));
    result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
    val model = accumulator.getTemplateModel();
    val xmlout = model.toXML
    
    //println("----- RAW ------")
    //val prettyPrinter = new PrettyPrinter(1000, 4)
    //println(prettyPrinter.format(xmlout))
    //println("----- x x x ------")
    
    val ecCount = (xmlout \ "com.genebio.nextprot.dataloader.context.ExperimentalContextWrapper").size
    println("OUTPUT - experimental context count:" + ecCount)
    assert(ecCount == expectedCount)
    
    val ecoList = (xmlout.child \ "wrappedBean" \ "detectionMethod" \ "cvName").toList
    println("OUTPUT - ecoCount:" + ecoList.size)
    assert(ecoList.size == expectedCount)
    ecoList.foreach(n => assert(n.text == "ECO:0000295[ACC]") )
    
    val tissueList = (xmlout.child \ "wrappedBean" \ "tissue" \ "cvName").toList
    println("OUTPUT - tissueCount:" + tissueList.size)
    assert(tissueList.size == expectedCount)
    
    val synoList = (xmlout.child \ "wrappedBean" \ "contextSynonyms" \\ "synonymName").toList
    println("OUTPUT - synoCount:" + synoList.size)
    synoList.foreach(n => assert(n.text.contains("eco->") && n.text.contains("tissue->")))
    
  }

  private def getExpectedCounts(xmlin: Elem) = {
    val consensusExpectedCount = ((xmlin \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "consensusTissue") })
      \ "data" filter dataToNotExclude("tissue")).size;
    val bloodExpectedCount = ((xmlin \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "blood") }) \ "data").size;
    val brainExpectedCount = ((xmlin \ "rnaExpression" filter { _ \\ "@assayType" exists (_.text == "humanBrain") })
      \ "data" filter dataToNotExclude("tissue")).size;
    val expectedCount = consensusExpectedCount + bloodExpectedCount + brainExpectedCount;

    (consensusExpectedCount, bloodExpectedCount, brainExpectedCount, expectedCount)
  }

  "The HPAExpcontextNXParser " should " find tissues in IHC and RNA expression and produce corresponding EC synonyms" in {

    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/hpa/expression/ENSG00000000003.xml";

    val xmlin = scala.xml.XML.loadFile(fname)
    val (consensusExpectedCount: Int, bloodExpectedCount: Int, brainExpectedCount: Int, expectedRNACount: Int) = getExpectedCounts(xmlin)
    val expectedIHCCount = (xmlin \ "tissueExpression" \\ "tissueCell").size
    val expectedCount = expectedRNACount + expectedIHCCount
    println("INPUT - count of tissue in RNA expression section: " + expectedRNACount)
    println("INPUT - count of tissue in IHC expression section: " + expectedIHCCount)
    println("INPUT - count of tissue in IHC + RNA expression section: " + expectedCount)  
    
    val result = hpaParser.parse(fname);
    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.txt")));
    result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
    val model = accumulator.getTemplateModel();
    val xmlout = model.toXML
    
    //println("----- RAW ------")
    //val prettyPrinter = new PrettyPrinter(1000, 4)
    //println(prettyPrinter.format(xmlout))
    //println("----- x x x ------")
    
    val ecCount = (xmlout \ "com.genebio.nextprot.dataloader.context.ExperimentalContextWrapper").size
    println("OUTPUT - experimental context count:" + ecCount)
    
    val synoList = (xmlout.child \ "wrappedBean" \ "contextSynonyms" \\ "synonymName").toList  // we may have N synonyms in 1 ec
    println("OUTPUT - synoCount:" + synoList.size)
    assert(synoList.size == expectedCount)

    synoList.foreach(n => assert(n.text.contains("eco->") && n.text.contains("tissue->")))

    val methods = synoList.groupBy(n => methodGroup(n.text)).map(el => (el._1, el._2.length)).toMap
    println("Synonyms by method:" + methods)
    assert(methods("IHC") == expectedIHCCount)
    assert(methods("RNA") == expectedRNACount)
   
  }

  def methodGroup(syn: String): String = {
      if (syn.contains("immunohistochemistry")) return "IHC";
      if (syn.contains("RNA-seq")) return "RNA";
      return "???";    
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
