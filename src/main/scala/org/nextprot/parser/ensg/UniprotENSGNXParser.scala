package org.nextprot.parser.ensg

import java.io.File

import org.nextprot.parser.core.NXParser

class UniprotENSGNXParser extends NXParser {

  var pInfo = ""

  def parsingInfo: String = pInfo

  def parse(filename: String): (String,String) = {

    val file = new File(filename)

    val uniprotEntry = scala.xml.XML.loadFile(file)
    val ensgs = ENSGUtils.getGeneIds(uniprotEntry, ",")

    val commaNum = ensgs.count(_ == ',')

    if (commaNum > 0)
      pInfo = String.valueOf(commaNum+1)
    else
      pInfo = "0"

    (file.getName.split('.')(0), ensgs)
  }
}
