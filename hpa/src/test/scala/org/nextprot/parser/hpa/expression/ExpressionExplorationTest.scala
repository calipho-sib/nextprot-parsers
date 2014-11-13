package org.nextprot.parser.hpa.expression

import org.scalatest._
import org.nextprot.parser.core.NXParser
import java.io.File
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue._
import org.nextprot.parser.hpa.subcell.constants.HPAValidationValue
import scala.util.matching.Regex
import scala.xml.PrettyPrinter
import java.io.FileWriter
import org.nextprot.parser.core.datamodel.TemplateModel
import akka.dispatch.Foreach
import scala.collection.mutable.MutableList
import org.nextprot.parser.hpa.subcell.HPAValidation
import org.nextprot.parser.hpa.subcell.constants.HPAAPEReliabilityValue
import org.nextprot.parser.hpa.subcell.rules.APEQualityRule
import org.nextprot.parser.hpa.subcell.rules.APEQualityRule
import org.nextprot.parser.hpa.subcell.rules.APEQualityRule2014Stats
import org.nextprot.parser.hpa.HPAUtils

class ExpressionExplorationTest extends HPAExpressionTestBase {

  /**
   * output excerpt:
   *
   * uniprotIds count:2 Q9NPA5,Q9NTW7 for file /Users/pmichel/data/hpa/20140121/ENS/G00/000/020/256/ENSG00000020256.xml
   * uniprotIds count:4 O95467,P63092,P84996,Q5JWF2 for file /Users/pmichel/data/hpa/20140121/ENS/G00/000/087/460/ENSG00000087460.xml
   * uniprotIds count:0  for file /Users/pmichel/data/hpa/20140121/ENS/G00/000/100/890/ENSG00000100890.xml
   *
   */

  "The HPAExpressionNXParser " should " deal with HPA files with zero or multiple uniprot ids" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";

    getFilesForParsing(dir).foreach(f => {
      val root = scala.xml.XML.loadFile(f)
      val allxrefs = (root \ "identifier" \ "xref").toList
      val xrefs = allxrefs.filter(xr => (xr \ "@db").text == "Uniprot/SWISSPROT").toList
      if (xrefs.size != 1) {
        val ids = xrefs.map(xr => (xr \ "@id").text).mkString(",")
        allxrefs.foreach(x => println("allxrefs count:" + allxrefs.size))
        println("uniprotIds count:" + xrefs.size + " " + ids + " for file " + f.getAbsolutePath())
      }
    })
  }

  /**
   * No such cases were found
   */
  "The HPAExpressionNXParser " should " or may deal with HPA files with zero or multiple tissueExpression elements" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    getFilesForParsing(dir).foreach(f => {
      val root = scala.xml.XML.loadFile(f)
      val alltes = (root \ "tissueExpression")
      alltes.size match {
        case 0 => println("No tissueExpression element for file " + f.getAbsolutePath())
        case 1 =>
        case _ => println("Multiple tissueExpression element for file " + f.getAbsolutePath())
      }
    })
  }

  /**
   * No such cases were found
   */
  "The HPAExpressionNXParser " should " or may deal with HPA files with tissueExpression elements with technology not equals to IH" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    getFilesForParsing(dir).foreach(f => {
      val root = scala.xml.XML.loadFile(f)
      val alltes = (root \ "tissueExpression")
      val tes = alltes.filter(el => (el \ "@technology").text != "IH").toList
      tes.size match {
        case 0 => None
        case _ => println("tissueExpression with technology " + tes.text + " for file " + f.getAbsolutePath())
      }
    })
  }

  /**
   * No such cases were found
   */
  "The HPAExpressionNXParser " should " or may deal with HPA files with tissueExpression elements with assayType not equals to tissue" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    getFilesForParsing(dir).foreach(f => {
      val root = scala.xml.XML.loadFile(f)
      val alltes = (root \ "tissueExpression")
      val tes = alltes.filter(el => (el \ "@assayType").text != "tissue").toList
      tes.size match {
        case 0 => None
        case _ => println("tissueExpression with technology " + (tes \ "@assayType").text + " for file " + f.getAbsolutePath())
      }
    })
  }

  /**
   * No such cases were found
   */
  "The HPAExpressionNXParser " should " throw an exception on unexpected tissue expression attributes" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    getFilesForParsing(dir).foreach(f => {
      val entryElem = scala.xml.XML.loadFile(f)
      val tesok = (entryElem \ "tissueExpression").
        filter(el => (el \ "@assayType").text == "tissue" && (el \ "@technology").text == "IH")
      if (tesok.size != 1) throw new NXException(CASE_ASSAY_TYPE_NOT_TISSUE)
    })
  }

  /**
   * HPA data are not ambiguous.
   * All files which are not APE (selected / single) for the tissue expression
   * have one and only one antibody describing tissueExpression for assayType = tissue
   */
  "The HPAExpressionNXParser " should " check that there is one and only one antibody having a tissueExpression with assayType equals to tissue if main tissue expression type is single or selected" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    var count: Integer = 0
    var pbm: Integer = 0
    getFilesForParsing(dir).foreach(f => {
      val entryElem = scala.xml.XML.loadFile(f)
      if ((entryElem \ "tissueExpression" \ "@type").text.toLowerCase() != "ape") {
        val all = (entryElem \ "antibody" \ "tissueExpression")
        val oks = all.filter(te => (te \ "@assayType").text == "tissue")
        count = count + 1
        if (oks.size != 1) {
          println("ambiguous single or selected antibody for tissueExpression in file " + f.getAbsolutePath())
          pbm = pbm + 1
        }
      }
    })
    println("files checked : " + count)
    println("files with pbm: " + pbm)
    if (pbm > 0) assert(false)

  }

  "The HPAExpressionNXParser " should " discard files where no antibody has a tissueExpression with assayType tissue" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    var count: Integer = 0
    var pbm: Integer = 0
    getFilesForParsing(dir).foreach(f => {
      val entryElem = scala.xml.XML.loadFile(f)
      count = count + 1
      try {
        HPAUtils.getAntibodyIdListForExpr(entryElem)
      } catch {
        case e: Exception => pbm = pbm + 1
      }
    })
    println("files checked : " + count)
    println("files with pbm: " + pbm)
    if (pbm > 0) assert(false)

  }

  "The HPAExpressionNXParser " should " discard files with type selected where multiple antibodies have a tissueExpression with assayType tissue" in {

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    var count: Integer = 0
    var pbm: Integer = 0
    getFilesForParsing(dir).foreach(f => {
      val entryElem = scala.xml.XML.loadFile(f)
      count = count + 1
      try {
        HPAQuality.getQuality(entryElem, "tissueExpression")
      } catch {
        case e: NXException => {
          if (e.getNXExceptionType == CASE_MORE_THAN_ONE_ANTIBODY_FOUND_FOR_SELECTED) {
            println("CASE_MORE_THAN_ONE_ANTIBODY_FOUND_FOR_SELECTED in file " + f.getAbsolutePath())
            pbm = pbm + 1
          } else {
            e.printStackTrace()
            assert(false)
          }
        }
      }
    })
    println("files checked : " + count)
    println("files with pbm: " + pbm)
    if (pbm > 0) assert(false)

  }

  "Full set of HPA data " should " show statistics about each APE quality rule applied" in {

    APEQualityRule2014Stats.reset

    val dir = "nowhere/Users/pmichel/data/hpa/20140121";
    //val dir = "src/test/resources/hpa/20140121";
    var count: Integer = 0
    var pbm: Integer = 0
    getFilesForParsing(dir).foreach(f => {
      count = count + 1
      try {
        new HPAExpressionNXParser().parse(f.getAbsolutePath());
      } catch {
        case e: Exception => {     pbm = pbm + 1  }
      }
    })
    println("files checked : " + count)
    println("files with pbm: " + pbm)

    APEQualityRule2014Stats.show

    val x = APEQualityRule(HPAAPEReliabilityValue.High, HPAValidationValue.Supportive)
    println(x.toString())
    
  }

  /**
   * useful for preliminary tests
   */
  def toList(file: File): String = {

    val entryElem = scala.xml.XML.loadFile(file)
    val url = (entryElem \ "@url").text
    val tissueExpression = entryElem \ "tissueExpression"
    val assayType = (tissueExpression \ "@assayType").text
    val technology = (tissueExpression \ "@technology").text
    val abtype = (tissueExpression \ "@type").text
    val verif = (tissueExpression \ "verification").text
    val abcount = (entryElem \ "antibody").size

    // we discard files where assay type is cancer
    if (assayType.equalsIgnoreCase("cancer")) {
      val msg = "Ignoring assayType:" + assayType + " => skipping " + file.getName();
      throw new NXException(CASE_ASSAY_TYPE_NOT_TISSUE, msg);
    }
    if (!technology.equalsIgnoreCase("IH")) {
      val msg = "Ignoring technology:" + technology + " => skipping " + file.getName();
      throw new NXException(CASE_ASSAY_TYPE_NOT_TISSUE, msg)
    }
    // for extracting only cases with multiple antibodies
    /*
	    if (abtype.equalsIgnoreCase("single")) {
	    	val msg = "Ignoring abtype:" + abtype + " => skipping "  + file.getName(); 
	    	throw new NXException(CASE_ASSAY_TYPE_CANCER, msg);
	    }
		*/
    val datalist: MutableList[String] = MutableList(url, assayType, technology, abtype, verif, abcount.toString)
    (entryElem \ "antibody").map(ab => {
      datalist += (ab \ "@id").text
      datalist += "wb:" + (ab \ "westernBlot" \ "verification").text
      datalist += "pa:" + (ab \ "proteinArray" \ "verification").text
      val te = (ab \ "tissueExpression").filter(someTe => (someTe \ "@assayType").text == "tissue" && (someTe \ "@technology").text == "IH")
      datalist += "te:" + (te \ "verification").text
    })
    datalist.mkString("|")

  }

}

