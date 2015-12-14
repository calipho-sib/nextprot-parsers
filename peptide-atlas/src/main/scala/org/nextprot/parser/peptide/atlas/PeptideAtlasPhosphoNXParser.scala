package org.nextprot.parser.peptide.atlas

import java.io.File
import org.nextprot.parser.core.NXParser
import scala.io.Source
import org.nextprot.parser.peptide.atlas.datamodel.Peptide
import org.nextprot.parser.peptide.atlas.datamodel.DbXref
import org.nextprot.parser.peptide.atlas.datamodel.Feature
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

class PeptideAtlasPhosphoNXParser extends App {

  var pInfo = ""
  var pep_count :Int = 0
  def parsingInfo: String = pInfo
  
  //val pepsMap: HashMap[String, ArrayBuffer[String]] = new HashMap();   
  //val pepsMap: HashMap[String, ArrayBuffer[String]]   
  
  def parse(filename: String) = {
  val pprinter=new scala.xml.PrettyPrinter(240, 2)
  val topHeader = "<entries version=\"201509\" datarelease=\"PeptideAtlas human phosphoproteome\" datasource=\"PeptideAtlas human phosphoproteome\">"
  // First parse the tsv file of sampleId-MDATA-pmid association and guild a map with sampleIds as keys
  //val sampleIdMap: HashMap[String, String] = PeptideAtlasUtils.getMetadataMap1("/home/agateau/workspace/nextprot-parsers/peptide-atlas/src/test/resources/org/nextprot/parser/peptide/atlas/Metadata_phosphosetPA.tsv"); 
  val sampleIdMap: HashMap[String, String] = PeptideAtlasUtils.getMetadataMap("/share/sib/common/Calipho/np/metadata_files/metadata.txt"); 

  // Now parse the data file and build a map with PaPids as key
   val pepsMap: HashMap[String, ArrayBuffer[String]] = new HashMap();    
   val src = Source.fromFile(filename)  
  //pepsMap = new HashMap()
    for(line <- src.getLines().drop(1)) {
      val key = line.substring(0,11)
      val pepRows = pepsMap.getOrElse(key, new ArrayBuffer());
      pepRows.append(line.substring(12));
      pepsMap.put(key, pepRows);
    }
   src.close() 
   
    var bigXML = topHeader
    println(topHeader)  
    // Iterate on the map
    for(pep <- pepsMap) {
     // pep._1 = key = PaPxxxxxxxx
     var featureList  = List[Feature]()
     var ftdbReflistSum = List[DbXref]()
     var pepdbReflist = List[DbXref]()
     val featuresPos: List[Int] = PeptideAtlasUtils.getDistinctModPos(pep._2)
     featuresPos.foreach { pos =>
       val ftdbReflist = PeptideAtlasUtils.getDBRefForFeature(pep._2, pos, sampleIdMap)
       val modRes = PeptideAtlasUtils.getModresForFeature(pep._2, pos) // could be quicker if we build first a map (pos,modres) at peptide level
       val feature= new Feature(_position=pos, _description=modRes, _pepid=pep._1, _dbrefs=ftdbReflist)
       ftdbReflistSum ++= ftdbReflist
       featureList = feature :: featureList
      }
     ftdbReflistSum.foreach { ftdbRef =>
       pepdbReflist = new DbXref(_mData=ftdbRef._mData, _pmid=ftdbRef._pmid, _quality=null ) :: pepdbReflist
       }
     val sequence = pep._2(0).split("\\s+")(0)
     val peptide= new Peptide(_sequence=sequence, id=pep._1, _dbrefs=pepdbReflist.distinct, _features=featureList)
     //println(peptide.toXML)
     val xmlpep = pprinter.format(peptide.toXML)
     //bigXML += xmlpep
     //println(xmlpep)
     }
    bigXML += "</entries>" // closed
    println("</entries>")
    
  //println(pepsMap.size + " peptides successfully parsed")
  this.pep_count = pepsMap.size
  }
  
}
