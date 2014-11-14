package org.nextprot.parser.hpa.antibody

import scala.xml.PrettyPrinter
import java.io.FileWriter
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.datamodel.antibody.AntibodyEntryWrapperList
import java.util.HashSet
import scala.collection.mutable.TreeSet
import org.nextprot.parser.core.stats.Stats
import org.nextprot.parser.core.NXProperties
import org.nextprot.parser.core.impl.NXPrettyReducer

/**
 * Implementation of a reducer where antibodies are not repeated
 */
class HPAAntibodyReducer extends NXPrettyReducer {

  val fw = new FileWriter(System.getProperty("output.file"), false)

  private val antibodyNames = new TreeSet[String]();

  def reduce(objects: Any) = {
    objects match {
      case data: AntibodyEntryWrapperList => {

        data.antibodyList.foreach(
          antibody => {
            val antibodyAccession = antibody._dbxref;
            if (!antibodyNames.contains(antibodyAccession)) {
              antibodyNames.add(antibodyAccession);
              fw.write(getPrettyFormatIfNeeded(antibody.toXML) + "\n");
              Stats.increment("ANTIBODY-COUNT", "NEW")
            } else {
              Stats.increment("ANTIBODY-COUNT", "REPEATED")
            }

          });
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