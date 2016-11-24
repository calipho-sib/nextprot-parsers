package org.nextprot.parser.core.impl

import java.io.FileWriter
import scala.xml.PrettyPrinter
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.constants.NXQuality
import org.nextprot.parser.core.stats.StatisticsCollector
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.core.NXProperties
import scala.xml.Elem
import scala.xml.Node

abstract class NXPrettyReducer extends NXReducer {

  val prettyPrinter = new PrettyPrinter(1000, 4);

  val prettyFormat = (System.getProperty(NXProperties.prettyPrint) != null)

  if (prettyFormat)
    println("Warning! Using pretty print.... This configuration is not performant and will take some time... (useful for debug)");
  else println("FYI - No pretty print is used,if you want to use pretty print use the system property \"-" + NXProperties.prettyPrint + "\" but be aware it is much slower....");

  final def getPrettyFormatIfNeeded(xml: Node): String = {
    if (prettyFormat)
      prettyPrinter.format(xml);
    else xml.toString;
  }
}
