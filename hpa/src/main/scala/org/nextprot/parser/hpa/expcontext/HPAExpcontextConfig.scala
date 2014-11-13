package org.nextprot.parser.hpa.expcontext

import java.io.File
import scala.io.Source
import scala.collection.immutable.Map

class CalohaMapEntry(line: String) {
  val list = line.split("\\|");
  val syn: String = list(0).toLowerCase();
  val name: String = list(1);
  val ac: String = list(2);
  override def equals(that: Any): Boolean = that match {
    case other: CalohaMapEntry => {
      syn == other.syn && name == other.name && ac == other.ac;
    }
    case _ => false
  }
  override def hashCode = (syn + "/" + name + "/" + ac).hashCode();
  override def toString(): String = {
    return "(syn:" + syn + " - name:" + name + " - ac:" + ac + ")";
  }
}

case class CalohaMapper(map: Map[String, CalohaMapEntry], errors: List[String])

object HPAExpcontextConfig {

  def getTissueEntry(x: Option[CalohaMapEntry]): CalohaMapEntry = x match {
    case Some(s) => s
    case None => null
  }


  /**
   * Loads a CalohaMapper from a file of which name is defined in a system property
   */
  def readTissueMapFile: CalohaMapper = {
    val propName = "hpa.tissue.mapping.file";
    val mappingFile = if (System.getProperty(propName) != null) new File(System.getProperty(propName)) else null;
    // check property for name of file to read and file existence
    if ((mappingFile == null)) {
      println(propName + " property is not set. Set environment " + propName + " system property");
      System.exit(1);
    }
    if (!mappingFile.exists()) {
      println("Mapping file " + mappingFile.getAbsolutePath() + " not found!.");
      System.exit(1);
    }
    readTissueMapFileFromFile(mappingFile)

  }

  /**
   * Loads a CalohaMapper from a file
   */
  def readTissueMapFileFromFile(mappingFile: File): CalohaMapper = {

    println("Loading tissue map file: " + mappingFile);

    // build map with key = synonym, value = full TissueEntry
    val lines = Source.fromFile(mappingFile).getLines().filter(!_.startsWith("#"))
    val tissues = lines.map(l => new CalohaMapEntry(l)).toArray;
    val syn2tissue = tissues.map(t => t.syn -> t).toMap;

    // sizes should be identical
    println("Number of tissues in data lines:" + tissues.size);
    println("Number of elements in syn2tissues:" + syn2tissue.size);

    // find any inconsistencies in the file, i.e.: 1 synonym -> N tissues 
    val unmapped = tissues.filter(t => !t.equals(getTissueEntry(syn2tissue.get(t.syn))))
    val problems = unmapped.map(t => {
      val o1 = getTissueEntry(syn2tissue.get(t.syn));
      println("");
      println("ERROR: Multiple lines for same synonym");
      println("Line stored in map : " + o1);
      println("Line found in file : " + t);
      println("");
      List("Multiple lines for synonym " + o1.toString, "Multiple lines for synonym " + t.toString)
    }).flatten.toList

    // just check that irrelevant synonym not found and that a relevant one is found
    assert(!syn2tissue.contains("jhkl"));
    assert(syn2tissue.contains("ovarian stroma"));

    return CalohaMapper(syn2tissue,problems)
    


  }

}