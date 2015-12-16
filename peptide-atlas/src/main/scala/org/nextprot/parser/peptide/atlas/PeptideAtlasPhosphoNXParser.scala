package org.nextprot.parser.peptide.atlas

import org.nextprot.parser.core.NXParser
import scala.io.Source
import org.nextprot.parser.peptide.atlas.datamodel.Peptide
import org.nextprot.parser.peptide.atlas.datamodel.DbXref
import org.nextprot.parser.peptide.atlas.datamodel.Feature
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

class PeptideAtlasPhosphoNXParser extends NXParser{

  var pInfo = ""
  var pep_count :Int = 0
  def parsingInfo: String = pInfo
  
  def parse(filename: String) : List[Peptide] = {
  // First parse the tsv file of sampleId-MDATA-pmid association and guild a map with sampleIds as keys
  //val sampleIdMap: HashMap[String, String] = PeptideAtlasUtils.getMetadataMap1("src/test/resources/org/nextprot/parser/peptide/atlas/Metadata_phosphosetPA.tsv") 
  val sampleIdMap: HashMap[String, String] = PeptideAtlasUtils.getMetadataMap("/share/sib/common/Calipho/np/metadata_files/metadata.txt"); 

  // Now parse the data file and build a map with PaPids as key
   val pepsMap: HashMap[String, ArrayBuffer[String]] = new HashMap();    
   val src = Source.fromFile(filename)  

    for(line <- src.getLines().drop(1)) {
      val key = line.substring(0,11)
      val pepRows = pepsMap.getOrElse(key, new ArrayBuffer());
      pepRows.append(line.substring(12));
      pepsMap.put(key, pepRows);
    }
   src.close() 
   
    // Iterate on the map
    val allPeps = pepsMap.map(pep =>  {
     // pep._1 = key = PaPxxxxxxxx
     var ftdbReflistSum = List[DbXref]()
     var pepdbReflist = List[DbXref]()
     val featuresPos: List[Int] = PeptideAtlasUtils.getDistinctModPos(pep._2)
     val featureList : List[Feature] = featuresPos.map ({ pos =>
       val ftdbReflist = PeptideAtlasUtils.getDBRefForFeature(pep._2, pos, sampleIdMap)
       val modRes = PeptideAtlasUtils.getModresForFeature(pep._2, pos) // could be quicker if we build first a map (pos,modres) at peptide level
       ftdbReflistSum ++= ftdbReflist
       new Feature(_position=pos, _description=modRes, _pepid=pep._1, _dbrefs=ftdbReflist)
      }).toList;
     
     ftdbReflistSum.foreach { ftdbRef =>
       pepdbReflist = new DbXref(_mData=ftdbRef._mData, _pmid=ftdbRef._pmid, _quality=null ) :: pepdbReflist
       }
     val sequence = pep._2(0).split("\\s+")(0)
     
     new Peptide(_sequence=sequence, id=pep._1, _dbrefs=pepdbReflist.distinct, _features=featureList)
    }).toList;
   
   this.pep_count = pepsMap.size
   return allPeps
  }
  
}
