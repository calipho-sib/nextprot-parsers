package org.nextprot.parser.hpa.expcontext

import java.io.File
import org.scalatest._
import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.NXParserApp
import org.nextprot.parser.core.exception.NXException
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.subcell.cases._
import org.nextprot.parser.hpa.HPAUtils
import org.nextprot.parser.hpa.HPAQuality
import org.nextprot.parser.hpa.commons.rules.APEQualityRule
import org.nextprot.parser.hpa.commons.constants.HPAValidationValue._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.core.datamodel.TemplateModel
import scala.xml.PrettyPrinter
import java.io.FileWriter

abstract class HPAExpcontextTestBase extends FlatSpec with Matchers {

  val directory = "src/test/resources/hpa/20140121/ENS/G00/000/180";
  //val directory = "/Users/pmichel/data/hpa/20140121";

  def filesAt(f: File): Array[File] = if (f.isDirectory) f.listFiles flatMap filesAt else Array(f)

  def getFilesForParsing: List[File] = {
    val regularExpression = "^ENSG.*.xml$"
    println("Looking for files like: " + regularExpression + " in directory: " + directory + " ...");
    return filesAt(new File(directory)).filter(f => regularExpression.r.findFirstIn(f.getName).isDefined).toList;
  }

  System.setProperty("hpa.tissue.mapping.file", "src/test/resources/NextProt_tissues.from-db.with2warnings.txt")

  //System.setProperty("hpa.mapping.file", "src/test/resources/HPA_Subcell_Mapping.txt")
  //System.setProperty("hpa.anti.multi.file", "src/test/resources/multi_target_antibodies.txt")
  
    def loopOnFiles(saveOutput: Boolean, someFunction: TemplateModel => Boolean): Boolean = {
    val files: List[File] = getFilesForParsing
    val prettyPrinter = new PrettyPrinter(1000, 4)
    var output: String = ""
    var i: Integer = 0
    var lastSize = 0
    val calohaMap = HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.txt"));
    val accumulator = new ExpcontextAccumulator(calohaMap);

    files.foreach(file => {
      val f = file.getAbsolutePath()
      try {
        val hpaParser = new HPAExpcontextNXParser();
        val r = hpaParser.parse(f)
        r.dataset.foreach(accumulator.accumulateCalohaMapping(_));
        val model = accumulator.getTemplateModel();

        val result = someFunction(model)
        if (!result) {
          println("Check function failed for file " + f)
          return false
        }
        output = prettyPrinter.format(model.toXML)
      } catch {
        case e: NXException => {
          if (e.getNXExceptionType == CASE_ASSAY_TYPE_NOT_TISSUE) {
            assert(true)
          } else {
            println("Unexpected NXException error case: " + e.getNXExceptionType.toString() + " with file " + f)
            return false
          }
        }
        case e: Exception => {
          println("Unexpected Exception in file " + f)
          e.printStackTrace()
          return false
        }
      }
      i = i + 1
      if (lastSize != output.length || i % 1000 == 0) {
        lastSize = output.length
        //println("files processed: " + i + ", output length: " + lastSize)
      }
    })
    //printing is not a test!!! 
/*    if (saveOutput) {
      val fw = new FileWriter("ExpContexts.xml", false)
      fw.write(output)
      fw.close
    }*/
    return true
  }


}

