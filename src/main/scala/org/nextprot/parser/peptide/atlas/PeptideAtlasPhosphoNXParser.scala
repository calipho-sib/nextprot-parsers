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
  val metadataMapfile = System.getProperty("xmetadatafile")

  def parsingInfo: String = pInfo
  
  def parse(filename: String) : List[Peptide] = {
    // First parse the mapping file of sampleId-MDATA-pmid association and build a map with sampleIds as keys
    // Mapping file can be either /share/sib/common/Calipho/np/metadata_files/metadata.txt or a tsv file delivered by Paula 
    val sampleIdMap: HashMap[String, String] = {
      if(this.metadataMapfile == null) PeptideAtlasUtils.getMetadataMap("src/test/resources/org/nextprot/parser/peptide/atlas/metadata.txt")
      else PeptideAtlasUtils.getMetadataMap(this.metadataMapfile)
    }

    // Now parse the tsv data file and build a map with PaPids as key
    val pepsMap: HashMap[String, ArrayBuffer[String]] = new HashMap();    
    val src = Source.fromFile(filename)  

    for(line <- src.getLines().drop(1)) {
      val key = line.substring(0,11)
      val pepRows = pepsMap.getOrElse(key, new ArrayBuffer());
      pepRows.append(line.substring(12));
      pepsMap.put(key, pepRows);
    }
    src.close() 
   
    // Iterate on the PaPid map
    var maxFTRefs = 0
    var maxPepRefs = 0
    var maxFTRefsId = ""
    var maxPepRefsId = ""
    val allPeps = pepsMap.map(pep =>  {
      // pep._1 = key = PaPxxxxxxxx
      var ftdbReflistSum = List[DbXref]()
      var pepdbReflist = List[DbXref]()
      val featuresPos: List[Int] = PeptideAtlasUtils.getDistinctModPos(pep._2)
      val featureList : List[Feature] = featuresPos.map ({ pos =>
        val ftdbReflist = PeptideAtlasUtils.getDBRefForFeature(pep._2, pos, sampleIdMap)
        val modRes = PeptideAtlasUtils.getModresForFeature(pep._2, pos) // could be quicker if we build first a map (pos,modres) at peptide level
        ftdbReflistSum ++= ftdbReflist // Collect all dbrefs referenced in features 
        if(ftdbReflist.size > maxFTRefs) {maxFTRefs = ftdbReflist.size; maxFTRefsId =  pep._1 + "-" + pos}
        new Feature(_position=pos, _description=modRes, _pepid=pep._1, _dbrefs=ftdbReflist)
      }).toList;
     
      ftdbReflistSum.foreach { ftdbRef => // Remove the 'quality' attribute since it is not relevant at the peptide level
        pepdbReflist = new DbXref(_mData=ftdbRef._mData, _pmid=ftdbRef._pmid, _quality=null ) :: pepdbReflist
      }
      val sequence = pep._2(0).split("\\s+")(0)
     
      if(pepdbReflist.distinct.size > maxPepRefs) {maxPepRefs = pepdbReflist.distinct.size; maxPepRefsId = pep._1}
      new Peptide(_sequence=sequence, id=pep._1, _dbrefs=pepdbReflist.distinct, _features=featureList) // Note the 'distinct'
      
    }).toList;
   
   //Console.err.println(maxFTRefs + " dbrefs in " + maxFTRefsId)
   //Console.err.println(maxPepRefs + " dbrefs in " + maxPepRefsId)
   this.pep_count = pepsMap.size
   return allPeps
  }
  
}
