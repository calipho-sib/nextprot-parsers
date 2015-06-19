package org.nextprot.parser.ensg

import java.io.FileWriter

import org.nextprot.parser.core.impl.NXPrettyReducer

class NXStringFileReducer extends NXPrettyReducer {

  val fw = new FileWriter(System.getProperty("output.file"), false)

  def reduce(objects: Any) = {
    objects match {
      case (uniprotEntry: String, ensgs: String) => {
        fw.append(uniprotEntry).append(" ").append(ensgs).append("\n")
      }
      case _ => throw new ClassCastException
    }
  }

  def start = {
  }

  def end = {
    fw.close;
  }
}
