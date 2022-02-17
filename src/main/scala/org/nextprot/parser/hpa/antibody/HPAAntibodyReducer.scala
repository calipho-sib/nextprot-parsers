package org.nextprot.parser.hpa.antibody

import org.nextprot.parser.core.datamodel.antibody.AntibodyEntryWrapperList
import org.nextprot.parser.core.impl.NXPrettyReducer
import org.nextprot.parser.core.stats.Stats

import java.io.FileWriter
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, TreeSet}

/**
 * Implementation of a reducer where antibodies are not repeated
 */
class HPAAntibodyReducer extends NXPrettyReducer {

  val fw = new FileWriter(System.getProperty("output.file"), false)

  val mappingfw = new FileWriter("mapping-ab-ensg.tsv", false)

  private val antibodyNames = new TreeSet[String]();
  private val antibodyNamesToEnsg = new mutable.HashMap[String, ListBuffer[String]]();

  def reduce(objects: Any) = {
    objects match {
      case data: AntibodyEntryWrapperList => {

        data.antibodyList.foreach(
          antibody => {
            val antibodyAccession = antibody._dbxref;
            mappingfw.write(antibodyAccession + "\t" + antibody._ensgAc + "\n")

            if (!antibodyNamesToEnsg.contains(antibodyAccession)) {
              fw.write(getPrettyFormatIfNeeded(antibody.toXML) + "\n");
              Stats ++ ("ANTIBODY-COUNT", "ANTIBODIES IN AT LEAST ONE ENSG")
            } else if ((antibodyNamesToEnsg.get(antibodyAccession).size == 1)) {
              // TODO: add other metrics for this case and count somehow
              // TODO: the nuber of multi-ensg antibodies BUT not add them to ANTIBODY-COUNT which is wrong !!!!
              Stats ++ ("ANTIBODY-COUNT", "ANTIBODIES IN SEVERAL ENSGs")
            }
            antibodyNamesToEnsg.getOrElseUpdate(antibodyAccession, ListBuffer()).append(antibody._ensgAc)

          });
      }
      case _ => throw new ClassCastException
    }
    println("####")
    antibodyNamesToEnsg.foreach {
      case (key, value) => println (key + "\t" + value.mkString(", ") + "\t" + value.length + "\n")
    }

  }

  def start = {
    fw.write("<object-stream>");
  }

  def end = {
    fw.write("</object-stream>");
    fw.close;
    mappingfw.close;
  }

}