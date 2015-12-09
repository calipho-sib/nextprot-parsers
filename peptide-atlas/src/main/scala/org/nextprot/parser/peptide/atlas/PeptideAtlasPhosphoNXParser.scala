package org.nextprot.parser.peptide.atlas

import java.io.File
import org.nextprot.parser.core.NXParser
import scala.io.Source
import org.nextprot.parser.peptide.atlas.datamodel.Peptide
import org.nextprot.parser.peptide.atlas.datamodel.DbXref
import org.nextprot.parser.peptide.atlas.datamodel.Feature
import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

class PeptideAtlasPhosphoNXParser extends App {

  var pInfo = ""
  //val topHeader = "<entries version="201509" datarelease="PeptideAtlas human phosphoproteome" datasource="PeptideAtlas human phosphoproteome">"
  val printer = new scala.xml.PrettyPrinter(80, 2)

  def parsingInfo: String = pInfo
  
  def parse(filename: String) = {
    // First parse the tsv file of sampleId-MDATA-pmid association and guild a map with sampleIds as keys
  val sampleIdMap: HashMap[String, String] = new HashMap(); 
  var mData = ""
  val src1 = Source.fromFile("/home/agateau/workspace/nextprot-parsers/peptide-atlas/src/test/resources/org/nextprot/parser/peptide/atlas/Metadata_phosphosetPA.tsv")  
    for(line <- src1.getLines().drop(1)) {
      val lineTokens = line.split("\\s+")
      if(lineTokens(0).startsWith("MDATA")) mData = lineTokens(0) + "-" + lineTokens(2) // group MDATA id and pmid
      sampleIdMap(lineTokens(1)) = mData
    }
  
  // Now parse the data file
  val pepsMap: TrieMap[String, ArrayBuffer[String]] = new TrieMap();    
  val src = Source.fromFile(filename)  
  
    for(line <- src.getLines().drop(1)) {
      val key = line.substring(0,11)
      val pepRows = pepsMap.getOrElse(key, new ArrayBuffer());
      pepRows.append(line.substring(12));
      pepsMap.put(key, pepRows);
    }

  for(pep <- pepsMap) {
     // pep._1 = key = PaPxxxxxxxx
     var featureList  = List[Feature]()
     var ftdbReflistSum = List[DbXref]()
     var pepdbReflist = List[DbXref]()
     var featuresPos: List[Int] = PeptideAtlasUtils.getDistinctModPos(pep._2)
     featuresPos.foreach { pos =>
       val ftdbReflist = PeptideAtlasUtils.getDBRefForFeature(pep._2, pos, sampleIdMap)
       val modRes = PeptideAtlasUtils.getModresForFeature(pep._2, pos) // could be quicker if we build first a map (pos,modres) at peptide level
       val feature= new Feature(_position=pos, _description=modRes, _dbrefs=ftdbReflist)
       ftdbReflistSum ++= ftdbReflist
       featureList = feature :: featureList
      }
     ftdbReflistSum.foreach { ftdbRef =>
       pepdbReflist = new DbXref(_mData=ftdbRef._mData, _pmid=ftdbRef._pmid, _quality=null ) :: pepdbReflist
       }
     val sequence = pep._2(0).split("\\s+")(0)
     val peptide= new Peptide(_sequence=sequence, id=pep._1, _dbrefs=pepdbReflist.distinct, _features=featureList)
     println(peptide.toXML)
     //val xml = printer.format(peptide.toXML)
     //printer.format(xml)
     //println(xml)
     }
  println(pepsMap.size + " peptides successfully parsed")
  }
  
}
