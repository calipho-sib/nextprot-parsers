package org.nextprot.parser.ensg

import java.io.File

import org.nextprot.parser.core.NXParser

import scala.xml.NodeSeq

class UniprotENSGNXParser extends NXParser {

  def parsingInfo: String = {
    return null
  }

  def parse(filename: String): (String,String) = {

    val file = new File(filename)

    val uniprotEntry = scala.xml.XML.loadFile(file)

    return (file.getName.split('.')(0), ENSGUtils.getGeneIds(uniprotEntry, ","))
  }
}

