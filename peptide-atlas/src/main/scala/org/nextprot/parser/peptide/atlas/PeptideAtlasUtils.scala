package org.nextprot.parser.peptide.atlas

import scala.collection.mutable.ArrayBuffer
import org.nextprot.parser.peptide.atlas.datamodel.DbXref
import scala.collection.mutable.HashMap
import util.control.Breaks._
import scala.io.Source

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
   
   def getModresForFeature(pepLines: ArrayBuffer[String], pos: Int): String = {
     
     pepLines.foreach { pepLine =>
      val pepLineTokens = pepLine.split("\\s+")
      if(pepLineTokens(2).toInt == pos) {
        return getMod(pepLineTokens(1),pos)
        }
      }
    "unknown"
   }
 
   def getDBRefForFeature(pepLines: ArrayBuffer[String], pos: Int, smap:HashMap[String, String]): List[DbXref] = {
     var dbref :DbXref = null
     var dbrefList  = List[DbXref]()
     var mdata_pmid = ""
     pepLines.foreach { pepLine =>
      val pepLineTokens = pepLine.split("\\s+")
      if(pepLineTokens(2).toInt == pos) {
        val quality = if(pepLineTokens(3).toFloat >= 0.99) "GOLD" else "SILVER"
        var sampleId = pepLineTokens(4)
        if(sampleId.endsWith("-")) sampleId = sampleId.dropRight(1) // Remove trailing dash
        if(!smap.contains(sampleId)) Console.err.println(sampleId + " not mapped...") 
        else {
        mdata_pmid = smap(sampleId)
        val mdataList = mdata_pmid.split("-")
        val mdata = mdataList(0)
        val pmid = if(mdataList.length > 1) mdataList(1) else null // Some datasets have no associated pmid
        dbref = new DbXref(_mData=mdata, _pmid=pmid, _quality=quality )
        dbrefList = dbref :: dbrefList }
        }
      }
    dbrefList.distinct
   }
   
   def getMetadataMap1(filename: String): HashMap[String, String] = {
     /* Sample input: col1=mdata_acc, col2=sampleId col3=pubmedId (Paula's file)
      * 
MDATA_0056  6526  26055452
  6526-heavy  
  6566  
  6566-heavy  
MDATA_0057  6402  24850871
  6403  
      * 
      */
     var mData = ""
     val sampleIdMap: HashMap[String, String] = new HashMap(); 
     val src = Source.fromFile(filename) 
     
    for(line <- src.getLines().drop(1)) {
      val lineTokens = line.split("\\s+")
      if(lineTokens(0).startsWith("MDATA")) mData = lineTokens(0) + "-" + lineTokens(2) // group MDATA id and pmid
      sampleIdMap(lineTokens(1)) = mData
    }
     src.close()
     sampleIdMap
   }
   
   def getMetadataMap(filename: String): HashMap[String, String] = {
     /* Sample input: (metadata.txt)
      * 
...
ID   Phosphoproteome of 11 cell lines derived from Burkitt, follicular and Mantle cell lymphomas.
AC   MDATA_0070
DM   GLOBAL. Mass spectrometry LC-MS/MS.
CL   GLOBAL. BJAB [CVCL_5711]; Raji [CVCL_0511]; Ramos [CVCL_0597]; FL-18 [CVCL_8093]; FL-318 [CVCL_8095]; OCI-Ly-1 [CVCL_1879]; SU-DHL-4 [CVCL_0539]; JeKo-1 [CVCL_1865]; NCEB-1 [CVCL_1875]; REC-1 [CVCL_1884]; UPN1 [CVCL_A795].
SP   GLOBAL. Protein reduction, alkylation, followed by digestion with trypsin. Peptides desalted by C18 solid phase extraction (SPE). Phosphopeptide enrichment by TiO2-MOAC (metal oxide affinity chromatography) followed by tyrosine-phosphorylated peptide immunoprecipitation. Eluents from TiO2-MOAC and phosphotyrosine immunoprecipitation were analyzed. 
IP   GLOBAL. Reverse phase nano-LC-MS/MS. Nanoscale C18 HPLC. MS Instrument: LTQ-Orbitrap XL and LTQ FT Ultra. Ionization: Nanoelectrospray ion source. Fragmentation: Collision-induced dissociation (CID).
DC   GLOBAL. Peptide identification. GOLD
DC   LOCAL. GOLD: PTM localization probability >= 0.99.
DC   LOCAL. SILVER: PTM localization probability >= 0.95.
DR   PubMed; 24667141.
CC   PeptideAtlas Phosphoset sampleID(s): 6508; 6552.
//  
... 
      */
     var mData = ""
     val sampleIdMap: HashMap[String, String] = new HashMap(); 
     val src = Source.fromFile(filename) 
     
    for(line <- src.getLines()) {
      val lineTokens = line.split("\\s+")
      if(lineTokens(0).startsWith("AC")) mData = lineTokens(1) + "-"
      if(line.startsWith("DR   PubMed;")) mData += lineTokens(2).dropRight(1) // group MDATA id and pmid
      if(line.startsWith("CC   PeptideAtlas Phosphoset sampleID")) {
        for(sampleId <- line.substring(line.indexOf(":")+2).dropRight(1).split("; ")) sampleIdMap(sampleId) = mData
      }
    }
     src.close()
     sampleIdMap
   }
   
}
