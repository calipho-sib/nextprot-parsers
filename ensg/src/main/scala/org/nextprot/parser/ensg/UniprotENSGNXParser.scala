package org.nextprot.parser.ensg

import java.io.File

import org.nextprot.parser.core.NXParser

import scala.xml.NodeSeq

class UniprotENSGNXParser extends NXParser {

  var pInfo : String = null;
  
  def parsingInfo: String = {
    return pInfo;
  }

  def parse(filename: String): String = {

    val file = new File(filename)

    val uniprotEntry = scala.xml.XML.loadFile(file)

    val accession = file.getName.split('.')(0)

    val ensemblIds = ENSGUtils.getGeneIds(uniprotEntry, ",")

    return accession+" "+ensemblIds;
  }
}

