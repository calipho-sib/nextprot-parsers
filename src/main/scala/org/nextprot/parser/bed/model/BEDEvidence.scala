package org.nextprot.parser.bed.model

import scala.collection.mutable.TreeSet

import org.nextprot.parser.bed.commons.NXCategory
import org.nextprot.parser.bed.commons.NXCategory._
import org.nextprot.parser.bed.commons.NXTerminology
import org.nextprot.parser.bed.commons.NXTerminology._
import org.nextprot.parser.bed.commons.BEDUtils
import org.nextprot.parser.bed.commons.BEDUtils.RelationInfo
import org.nextprot.parser.bed.BEDConstants
import org.nextprot.parser.bed.service.GeneNameServiceCached

case class BEDEvidence(
  val _annotationAccession: String,
  val _subject: String,
  val _relation: String,
  val _bedObjectCvTerm: BEDCV,
  val _bioObject: String,
  val _bioObjectType: String,
  val intensity: String,
  val isNegative: Boolean,
  val _quality: String,
  val subjectProteinOrigin: String,
  val objectProteinOrigin: String,
  val ecoString: String,
  val vdAllels: List[String],
  val mgiAllels: List[String],
  val txtAllels: List[String],
  val references: List[(String, String)]) {

  def extractVDFromTxtAllel(text: String): String = {
    val i = text.indexOf("VDSubject:");
    if (i != -1) {
      return text.substring(i + 10).trim();
    } else null
  }

  def getSubjectAllelsWithNote: (Set[String], String) = {

    val subjectAllels = new TreeSet[String]();
    var note: String = null;

    if (mgiAllels.filter(m => !m.toLowerCase().startsWith("tg(")).size > 1) {
      note = "Taking single allele for MGI multiple mutants, should fix this by adding VDSubject in TXT" + mgiAllels.filter(m => !m.toLowerCase().startsWith("tg("))
      //throw new RuntimeException("This case should not happen");
      //subjectAllels.add(vdAllels(0));
    } else if (vdAllels.size > 1) { // Multiple mutants VD
      vdAllels.foreach(v => {
        subjectAllels.add(v);
      });
    } else if (txtAllels.size > 1) { // Multiple mutants TXT
      txtAllels.foreach(t => {
        val vdAllel = extractVDFromTxtAllel(t);
        if (vdAllel != null) {
          vdAllel.split("\\+").toList.map(_.trim()).foreach { v => subjectAllels.add(v) };
        }
      });
    } else {
      subjectAllels.add(_subject);
    }

    if (subjectAllels.isEmpty) {
      note += "Adding single subject for  " + _annotationAccession + " please fix";
      subjectAllels.add(_subject);
    }

    val subjectGene = _subject.substring(0, _subject.indexOf("-")).toLowerCase();
    
    val subjectAllelsSet = subjectAllels.toSet;
    val response = subjectAllelsSet.filter { a => a.toLowerCase().startsWith(subjectGene)}.toSet;
    
    if(subjectAllelsSet.size != response.size){
      //We don't know how to deal wtih subjects on multiple genes, therefore we remove the subjects which don't belong to the gene
      note += "removing one allele for multiple genes " + _annotationAccession + " set: " + subjectAllelsSet + " filtered set " + response + " subject: " + _subject + " gene name " + subjectGene;
      println(note);
    }
    
    return (response, note);

  }

  def getNXCvTermAccession(): String = {
    return _bedObjectCvTerm.accession;
  }

  def getNXCvTermCvName(): String = {
    return _bedObjectCvTerm.cvName;
  }

  def getNXCategory(): NXCategory.Value = {

    if (_bedObjectCvTerm.category.equals("Gene Ontology")) {
      val subcategory = _bedObjectCvTerm.subCategory;
      subcategory match {
        case "Gene Ontology molecular function" => GoMolecularFunction;
        case "Gene Ontology biological process" => GoBiologicalProcess;
        case "Gene Ontology cellular component" => GoCellularComponent;
        case _ => throw new Exception("not expecting category " + subcategory + _bedObjectCvTerm);
      }
    } else {
      val categories = BEDUtils.getRelationInformation(_relation, isNegative).getAllowedCategories();
      if (categories.size != 1) {
        throw new Exception("Expected one possible category for " + _relation + " " + isNegative + " found: " + categories + " term :" + _bedObjectCvTerm.category)
      } else {
        val category = categories(0);
        if (category.equals(NXCategory.BinaryInteraction)) {
          if (_bioObjectType.equals("chemical")) {
            return NXCategory.SmallMoleculeInteraction;
          }
        }
        category;
      }
    }
  }

  def getNXBioObject(): String = {
    if (getRelationInfo.getBioObject) {
      if(_bioObjectType.equals("protein")){
          return GeneNameServiceCached.getNXAccessionForGeneName(_bioObject);
      }
      return _bioObject;
    } else return null;
  }

  def getNXTerminology(): NXTerminology.Value = {

    if (_bedObjectCvTerm.category.equals("Gene Ontology")) {
      val subcategory = _bedObjectCvTerm.subCategory;
      subcategory match {
        case "Gene Ontology molecular function" => GoMolecularFunctionCv;
        case "Gene Ontology biological process" => GoBiologicalProcessCv;
        case "Gene Ontology cellular component" => GoCellularComponentCv;
        case _ => throw new Exception("not expecting terminology " + subcategory);
      }
    } else {
      val terminologies = BEDUtils.getRelationInformation(_relation, isNegative).getAllowedTerminologies();
      if (terminologies.size == 1) {
        terminologies(0);
      } else if (terminologies.size > 1) {
        throw new Exception("Expected one possible terminology for " + _relation + " " + isNegative + " found: " + terminologies)
      } else {
        null; // Not terminology
      };
    }
  }

  def isVP(): Boolean = {
    return _annotationAccession.contains("CAVA-VP");
  }

  def isProteinProperty(): Boolean = {
    return _relation.toLowerCase().contains("protein property");
  }

  def isMammalianPhenotype(): Boolean = {
    return _relation.toLowerCase().contains("phenotype");
  }
    
  def isInteraction(): Boolean = {
    return _relation.toLowerCase().contains("binding");
  }

  def isBinaryInteraction(): Boolean = {
    return (_relation.toLowerCase().contains("binding") && "protein".equals(_bioObjectType));
  }

  
  def isRegulation(): Boolean = {
    return (_relation.toLowerCase().contains("regulat"));
  }

    
  def isGO(): Boolean = {
    return _bedObjectCvTerm.category.equals("Gene Ontology");
  }

  def getReferences: List[(String, String)] = {
    return references;
  }

  def getRealObject(): String = {
    return (if (_bedObjectCvTerm != null) { _bedObjectCvTerm.cvName } else "") + _bioObject;
  }

  def getRelationInfo(): RelationInfo = {
    return BEDUtils.getRelationInformation(_relation, isNegative);
  }
  
  def getEvidenceCode(): String = {
    if(!"Homo sapiens".equalsIgnoreCase(subjectProteinOrigin)){
      return "ECO:0000250"; //When subject is not human, then add ECO ISS (sequence similarity)
    }else {
      return "ECO:0000006"; //Experimental evidence
    }
  }

  def getEvidenceNote(): String = {
    return "Additional experimental evidence:" + ecoString + "\n"
  }
   

  def getPubmedId(): String = {
    return references.filter(_._1.equals("PubMed")).map(_._2).toList.mkString(",")
  }
  
  def getCrossRef(): String = {
    return references.filter(!_._1.equals("PubMed")).map(_._2).toList.mkString(",")
  }

}
