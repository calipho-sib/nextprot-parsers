package org.nextprot.parser.core.impl

import java.io.FileWriter
import scala.xml.PrettyPrinter
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality

class NXSimpleFileReducer extends NXReducer {

  private val prettyPrinter = new PrettyPrinter(1000, 4);
  private val fw = new FileWriter(System.getProperty("output.file"), false)

  def reduce(objects: Any) = {
    objects match {
      case tm: TemplateModel => {
        fw.write(prettyPrinter.format(tm.toXML) + "\n")
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
