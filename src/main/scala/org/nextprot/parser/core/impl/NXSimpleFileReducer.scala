package org.nextprot.parser.core.impl

import java.io.FileWriter
import scala.xml.PrettyPrinter
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.stats.StatisticsCollector
import org.nextprot.parser.core.stats.StatisticsCollectorSingleton
import org.nextprot.parser.core.NXProperties

class NXSimpleFileReducer extends NXReducer {

  val prettyFormat = (System.getProperty(NXProperties.prettyPrint) != null)
  
  if(prettyFormat)
    println("Warning! Using pretty print.... This configuration is n ot performant and will take some time... (useful for debug)");
  else   println("Not using pretty print (much more performant)! If you want to use pretty print use the system property " + NXProperties.prettyPrint);
  
  val prettyPrinter = new PrettyPrinter(1000, 4);
  val fw = new FileWriter(System.getProperty("output.file"), false)

  def reduce(objects: Any) = {
    objects match {
      case tm: TemplateModel => {
        val text = if(prettyFormat) prettyPrinter.format(tm.toXML) else tm.toXML;
        fw.write(text + "\n")
        StatisticsCollectorSingleton.increment("ENTRIES-QUALITY", tm.getQuality.toString())
      }
      case _ => throw new ClassCastException
    }
  }

  def start = {
    fw.write("<object-stream>");
  }

  def end = {
    fw.write("</object-stream>");
    fw.close;
  }

}
