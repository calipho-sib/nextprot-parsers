package org.nextprot.parser.peptide.atlas

import scala.xml.NodeSeq
import scala.collection.mutable.ArrayBuffer
import org.nextprot.parser.peptide.atlas.datamodel.DbXref
import scala.collection.mutable.HashMap
import util.control.Breaks._

object PeptideAtlasUtils {

   def getMod(modSeqOrg: String, modPos: Integer): String = {
     // modSeqOrg like n[145]RLS[167]EDYGVLK[272] or RLS[167]EDY[243]GVLK
     var modSeq = modSeqOrg
     var modString = ""
     var modRes = "unknown"
     var i = 0
     var j = 0
     
     // get rid of N-terminus if any
     if(modSeqOrg.startsWith("n")) modSeq = modSeqOrg.substring(modSeqOrg.indexOf("]") + 1)
     breakable {
     for(char <- modSeq) {
       i+=1
      if(char.isLetter) {
      j+=1
      if(j == modPos) {modString = modSeq.substring(i+1,i+4); break}
      }
    }
     }
     if(modString == "167") modRes = "phosphoserine"
     else if(modString == "181") modRes = "phosphothreonine"
     else if(modString == "243") modRes = "phosphotyrosine"
    modRes
   }
   
   def getDistinctModPos(pepLines: ArrayBuffer[String]): List[Int] = {
    // DFFLANASR  n[145]DFFLANAS[167]R  8   1.000   6549-   P07384 <- position is token 2 
     var posList  = List[Int]()
     pepLines.foreach { pepLine => 
      val pos = pepLine.split("\\s+")(2).toInt
      posList = pos :: posList
      }
    return(posList.distinct)
   }
   
   def getDBRefForFeature(pepLines: ArrayBuffer[String], pos: Int, smap:HashMap[String, String]): List[DbXref] = {
     var dbref :DbXref = null
     var dbrefList  = List[DbXref]()
     var mdata_pmid = ""
     pepLines.foreach { pepLine =>
      val pepLineTokens = pepLine.split("\\s+")
      if(pepLineTokens(2).toInt == pos) {
        var quality = "SILVER"
        if(pepLineTokens(3).toFloat >= 0.99) quality = "GOLD"
        var sampleId = pepLineTokens(4)
        if(sampleId.endsWith("-")) sampleId = sampleId.dropRight(1) // Remove trailing dash
        if(!smap.contains(sampleId)) println(sampleId + " not mapped...")
        else {
        mdata_pmid = smap(sampleId)
        val mdataList = mdata_pmid.split("-")
        val mdata = mdataList(0)
        var pmid :String = null
        if(mdataList.length > 1) pmid = mdataList(1)
        dbref = new DbXref(_mData=mdata, _pmid=pmid, _quality=quality )
        dbrefList = dbref :: dbrefList }
        }
      }
    dbrefList.distinct
   }
   
   def getModresForFeature(pepLines: ArrayBuffer[String], pos: Int): String = {
     
     pepLines.foreach { pepLine =>
      val pepLineTokens = pepLine.split("\\s+")
      if(pepLineTokens(2).toInt == pos) {
        return getMod(pepLineTokens(1),pos)
        }
      }
    "unknown"
   }
   
   
}
