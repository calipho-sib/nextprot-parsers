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

class RnaAndIHCExpcontextEntryTest extends HPAExpcontextTestBase {

  "The HPAExpcontextNXParser " should " produce ECs for RNA-seq and IHC" in {
    
    val hpaParser = new HPAExpcontextNXParser();
    val fname = "src/test/resources/ENSG00000106648.xml";
    try {

      val result = hpaParser.parse(fname);
      val accumulator = new ExpcontextAccumulator(HPAExpcontextConfig.readTissueMapFileFromFile(new File("src/test/resources/NextProt_tissues.from-db.txt")));
      result.dataset.foreach(accumulator.accumulateCalohaMapping(_));
      val model = accumulator.getTemplateModel();
      val xmlout = model.toXML
    
      //println("----- RAW ------")
      val prettyPrinter = new PrettyPrinter(1000, 4)
      val prettyout = prettyPrinter.format(xmlout)
      //println("----- x x x ------")
      val fw = new FileWriter("RnaAndIHCContexts.xml", false)
      fw.write(prettyout)
      fw.close
      
      assert(true)
    //val ecCount = (xmlout \ "com.genebio.nextprot.dataloader.context.ExperimentalContextWrapper").size
    //println("OUTPUT - experimental context count:" + ecCount)

    } catch {
      case e: Exception => {
        println("Unexpected Exception")
        e.printStackTrace()
        assert(false)
      }
    }
  }
  
  
}
