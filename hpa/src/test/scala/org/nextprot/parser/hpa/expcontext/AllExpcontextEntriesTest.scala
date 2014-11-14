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

class AllExpcontextEntriesTest extends HPAExpcontextTestBase {

  //override val directory = "/Users/pmichel/data/hpa/20140121";
  System.setProperty("hpa.tissue.mapping.file", "src/test/resources/NextProt_tissues.from-db.txt")

  "The HPAExpcontextNXParser " should " parse all HPA files and generate a XML output" in {

    val files: List[File] = getFilesForParsing
    val prettyPrinter = new PrettyPrinter(1000, 4)
    val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFile);
    var output: String = ""
    var i: Integer = 0
    var lastSize = 0
    files.foreach(file => {
      val f = file.getAbsolutePath()
      try {
        val hpaParser = new HPAExpcontextNXParser();
        val result = hpaParser.parse(f)

        result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
        val model = accumulator.getTemplateModel();

        output = prettyPrinter.format(model.toXML)
      } catch {
        case e: NXException => {
          if (e.getNXExceptionType == CASE_ASSAY_TYPE_NOT_TISSUE) {
            assert(true)
          } else {
            println("Unexpected NXException error case: " + e.getNXExceptionType.toString() + " with file " + f)
            assert(false)
          }
        }
        case e: Exception => {
          println("Unexpected Exception in file " + f)
          e.printStackTrace()
          assert(false)
        }
      }
      i = i + 1
      if (lastSize != output.length || i % 1000 == 0) {
        lastSize = output.length
        println("files processed: " + i + ", output length: " + lastSize)
      }
    })
    println("files processed: " + i + ", output length: " + lastSize)
    val fw = new FileWriter("ExpContexts.xml", false)
    fw.write(output)
    fw.close
    println("end")
  }

}
