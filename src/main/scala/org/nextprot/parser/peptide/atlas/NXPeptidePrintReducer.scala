package org.nextprot.parser.peptide.atlas

import java.io.FileWriter

import org.nextprot.parser.core.impl.NXPrettyReducer
import org.nextprot.parser.peptide.atlas.datamodel.Peptide

class NXPeptidePrintReducer extends NXPrettyReducer {
  val dataversion = System.getProperty("version")
  val topHeader = "<entries version=\"" + dataversion + "\" datarelease=\"PeptideAtlas human phosphoproteome\" datasource=\"PeptideAtlas human phosphoproteome\">"
  val fw = new FileWriter(System.getProperty("output.file"), false)

  def reduce(objects: Any) = {
    val peptides = objects.asInstanceOf[List[Peptide]]
    peptides.foreach { peptide => fw.append(getPrettyFormatIfNeeded(peptide.toXML) + "\n") }
  }

  def start = {
    fw.write(topHeader + "\n")
  }

  def end = {
    fw.write("\n</entries>")
    fw.close;
  }
}
