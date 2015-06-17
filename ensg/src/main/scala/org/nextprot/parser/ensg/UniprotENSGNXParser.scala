package org.nextprot.parser.ensg

import java.io.File

import org.nextprot.parser.core.NXParser
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.core.datamodel.TemplateModel

class UniprotENSGNXParser extends NXParser {

  var pInfo : String = null;
  
  def parsingInfo: String = {
    return pInfo;
  }
  

  def parse(fileName: String): TemplateModel = {

    val entryElem = scala.xml.XML.loadFile(new File(fileName))

    return  new DBReference("jell", "jeraw", null);
  }
}

sealed class DBReference(val _type: String, val _id: String, val _properties: List[String]) extends TemplateModel {

  override def toXML =    <dbReference type={ _type }/>

  override def getQuality: NXQuality = null;

}