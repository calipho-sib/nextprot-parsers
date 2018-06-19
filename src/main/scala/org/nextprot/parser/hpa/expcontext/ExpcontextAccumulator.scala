package org.nextprot.parser.hpa.expcontext

import scala.collection.mutable.Map
import java.util.TreeMap
import java.util.TreeSet
import scala.collection.mutable.Set
import org.nextprot.parser.core.datamodel.TemplateModel
import org.nextprot.parser.core.datamodel.annotation.ExperimentalContextWrapper
import org.nextprot.parser.core.datamodel.annotation.ExperimentalContextSynonym
import org.nextprot.parser.core.datamodel.annotation.ExperimentalContextListWrapper
import scala.collection.mutable.MutableList
import akka.dispatch.Foreach
import org.nextprot.parser.core.constants.EvidenceCode

class ExpcontextAccumulator(val calohaMapper: CalohaMapper) {

  val problems: Set[String] = Set()
  val accu: TreeMap[String, (String, TreeSet[SynoRule])] = new TreeMap()

  calohaMapper.errors.foreach(problems += _)
  
  // accumulates synonyms for each tissue
  def accumulate(ted: TissueExpressionData, cme: CalohaMapEntry, rule:String) = {
    if (!accu.containsKey(cme.ac)) accu.put(cme.ac, (cme.name, new TreeSet[SynoRule]()))
    val (_, s) = accu.get(cme.ac)
    if(ted.cellType==null) { // RNA expression has no cell types
      val sr = new SynoRule(HPAExpcontextUtil.getSynonymForXml(ted, EvidenceCode.RnaSeq), rule)
      s.add(sr)
    }
    else {
      val sr = new SynoRule(HPAExpcontextUtil.getSynonymForXml(ted, EvidenceCode.ImmunoHistochemistry), rule)
      s.add(sr)
    }
  }

  // for mapping caloha: try to match with ti + ct first and then with ct only 
  def accumulateCalohaMapping(ted: TissueExpressionData) = {
    val ti = HPAExpcontextUtil.getCleanTissue(ted.tissue);
    if (ted.cellType == null) {
      calohaMapper.map.get(ti) match {
        case Some(cme) => { accumulate(ted, cme, "partial match: no cell type") }
        case None => { problems.add("Mapping not found for " + ted.toString) }
      }
    } else {
      val ct = HPAExpcontextUtil.getCleanCellType(ted.cellType);
      calohaMapper.map.get(ti + " " + ct) match {
        case Some(cme) => { accumulate(ted, cme, "full match") }
        case None => {
          calohaMapper.map.get(ct) match {
            case Some(cme) => { accumulate(ted, cme, "partial match: no tissue") }
            case None => { problems.add("Mapping not found for " + ted.toString) }
          }
        }
      }
    }
  }

  def showProblems() = {
    println("--------------------")
    println("Problems in accu:")
    problems.foreach(println(_))
    println("total problems:" + problems.size);
    println("--------------------")
  }

  def showUnmappedTeds() = {
    println("--------------------")
    problems.foreach(println(_));
    println("total problems:" + problems.size);
    println("--------------------")
  }

  def showState(fname: String) = {
    println("total problems:" + problems.size + " - total accumula:" + accu.size + " - file:" + fname);
  }

  /*
    def showAccu() = {
		accu.foreach(e => {
			val ac = e._1;
			val name = e._2._1;
			println(ac + " - " + name)
			e._2._2.foreach(syn => println("  " + syn));
		})
    }
    def getTemplateModel(): TemplateModel = {
      val ecwlist = accu.map(e => {
			val ac = e._1; // unused ! but dont want to ignore it
			val name = e._2._1;
			val syns = e._2._2;
			val ecslist = syns.map(new ExperimentalContextSynonym(_)).toList
			new ExperimentalContextWrapper(name, ecslist)
      }).toList;
      val eclw = new ExperimentalContextListWrapper(ecwlist, problems.toList.sorted)
      return eclw
    } 
    */

  def getTemplateModel(): TemplateModel = {
    val ecwlist1 = MutableList[(String, (String, List[String]))]()
    val it = accu.keySet().iterator()
    while (it.hasNext()) {
      val ac: String = it.next()
      val (name, syns) = accu.get(ac)
      val synlist: MutableList[String] = MutableList()
      val it2 = syns.iterator()
      while (it2.hasNext()) { 
        val sr: SynoRule = it2.next()
        synlist += sr.syno
      }
      val el = (ac, (name, synlist.toList))
      ecwlist1 += el
    }
    val ecwlist = ecwlist1.map(e => {
      val ac = e._1; // unused ! but dont want to ignore it
      val tissue = e._2._1;
      val syns = e._2._2;
      val ecslist = syns.map(new ExperimentalContextSynonym(_)).toList
      if(syns(0).contains("RNA-seq")) 
        new ExperimentalContextWrapper(tissue, EvidenceCode.RnaSeq.code , EvidenceCode.RnaSeq.name ,ecslist)
      else
        new ExperimentalContextWrapper(tissue, EvidenceCode.ImmunoHistochemistry.code , EvidenceCode.ImmunoHistochemistry.name ,ecslist)
    }).toList;
    val eclw = new ExperimentalContextListWrapper(ecwlist.toList, problems.toList.sorted)
    return eclw
  }

}