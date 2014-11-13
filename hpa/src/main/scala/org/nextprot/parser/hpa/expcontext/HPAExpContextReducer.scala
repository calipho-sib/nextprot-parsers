package org.nextprot.parser.hpa.expcontext

import scala.xml.PrettyPrinter
import java.io.FileWriter
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.impl.NXPrettyReducer

class HPAExpContextReducer extends NXPrettyReducer {

  private val fw = new FileWriter(System.getProperty("output.file"), false)
  private val calohaMapper = HPAExpcontextConfig.readTissueMapFile
  private val accumulator = new ExpcontextAccumulator(calohaMapper)

  def reduce(objects: Any) = {
    objects match {
      case data: TissueExpressionDataSet => {
        data.dataset.foreach(accumulator.accumulateCalohaMapping(_));
      }
      case _ => throw new ClassCastException
    }
  }

  def start = {
  }

  def end = {
    if (calohaMapper.errors.size>0) {
        println("ERROR(S) in caloha mapping file:")
    	calohaMapper.errors.foreach(println(_))
    }
    val text = getPrettyFormatIfNeeded(accumulator.getTemplateModel().toXML);
    fw.write(text);
    fw.close;
    
  }

}