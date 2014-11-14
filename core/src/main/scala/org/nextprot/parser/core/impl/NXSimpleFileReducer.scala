package org.nextprot.parser.core.impl

import java.io.FileWriter
import scala.xml.PrettyPrinter
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.stats.StatisticsCollector
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.core.NXProperties

class NXSimpleFileReducer extends NXPrettyReducer {

  val fw = new FileWriter(System.getProperty("output.file"), false)

  def reduce(objects: Any) = {
    objects match {
      case tm: TemplateModel => {
        fw.write(getPrettyFormatIfNeeded(tm.toXML) + "\n")
        Stats ++ ("ENTRIES-QUALITY", tm.getQuality.toString())
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
