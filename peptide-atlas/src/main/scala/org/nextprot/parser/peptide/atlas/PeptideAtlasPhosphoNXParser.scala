package org.nextprot.parser.peptide.atlas

import java.io.File
import org.nextprot.parser.core.NXParser
import scala.io.Source

class PeptideAtlasPhosphoNXParser extends NXParser {

  var pInfo = ""

  def parsingInfo: String = pInfo

  def parse(filename: String) = {

    val file = new File(filename)
    val src = Source.fromFile(file);
    val iter = src.getLines().drop(1).map(_.split("\t"));
    
    println(iter.size);
    iter.foreach (a => println(a(2)))


  }
}
