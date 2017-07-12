package org.nextprot.parser.hpa.expcontext

import scala.xml.PrettyPrinter
import java.io.FileWriter
import org.nextprot.parser.core.NXReducer
import org.nextprot.parser.core.impl.NXPrettyReducer
import akka.dispatch.Foreach
import scala.collection.Map

class HPAExpContextReducer extends NXPrettyReducer {

  private val fw = new FileWriter(System.getProperty("output.file"), false)
  private val calohaMapper = HPAExpcontextConfig.readTissueMapFile
  private val accumulator = new ExpcontextAccumulator(calohaMapper)

  def reduce(objects: Any) = {
    objects match {
      case data: TissueExpressionDataSet => {
        data.dataset.foreach(accumulator.accumulateCalohaMapping(_));
      }
      case _ => throw new ClassCastException
    }
  }

  def start = {
  }

  def saveMappingInfo = {

   val fname = "tissue-mapping.tsv"
   val fw = new FileWriter(fname, false)
   println("Tissue mapping info saved in file: " + fname)
    
    val sep = "\t"
    val hdr = "Caloha AC" + sep + "Caloha name" + sep + "HPA tissue" + sep + "HPA cell type" + sep + "Rule";
    fw.write(hdr + "\n")
    val it = accumulator.accu.keySet().iterator()
    while (it.hasNext()) {
      val ac: String = it.next()
      val (name, syns) = accumulator.accu.get(ac)
      val it2 = syns.iterator()
      while (it2.hasNext()) { 
        var ct = ""
        val sr: SynoRule = it2.next()
        //Console.err.println("srsyno: " + sr.syno);
        val ti = sr.syno.split(";")(1).split("->")(1)
        if(sr.syno.split(";").size > 2) // RNAseq data have no cell type
          ct = sr.syno.split(";")(2).split("->")(1)
        val lin = ac + sep + name + sep + ti + sep + ct + sep + sr.rule;
        fw.write(lin + "\n")
      }
    }
    fw.close;
  }
  
  def end = {
        
    saveMappingInfo
    
    
    println("----------------------")
    println("Tissue mapping errors:")
    println("----------------------")
    accumulator.problems.foreach(p => println("ERROR " + p))
    println("count:"+accumulator.problems.size)
    
    if (calohaMapper.errors.size>0) {
	    println("----------------------")
	    println("Caloha mapping errors:")
	    println("----------------------")
    	println(calohaMapper)
        println("ERROR(S) in caloha mapping file:")
    	calohaMapper.errors.foreach(e => println("ERROR " + e))
    }
    println("----------------------")    
    saveMappingInfo
    
    val text = getPrettyFormatIfNeeded(accumulator.getTemplateModel().toXML);
    fw.write(text);
    fw.close;
    
  }
  
  

}