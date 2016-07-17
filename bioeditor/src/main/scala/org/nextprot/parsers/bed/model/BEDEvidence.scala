package org.nextprot.parsers.bed.model

import scala.collection.mutable.TreeSet

import org.nextprot.parsers.bed.commons.NXCategory
import org.nextprot.parsers.bed.commons.NXCategory._
import org.nextprot.parsers.bed.commons.NXTerminology
import org.nextprot.parsers.bed.commons.NXTerminology._
import org.nextprot.parsers.bed.service.OntologyService
import org.nextprot.parsers.bed.commons.BEDUtils
import org.nextprot.parsers.bed.commons.BEDUtils.RelationInfo

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
  val proteinOriginSpecie: String,
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

    return (subjectAllels.toSet, note);

  }

  def getNXCvTermAccession(): String = {
    return _bedObjectCvTerm.accession;
  }

  def getNXCvTermCvName(): String = {
    return _bedObjectCvTerm.cvName;
  }

  def getNXCategory(): NXCategory.Value = {

    if (_bedObjectCvTerm.category.equals("Gene Ontology")) {
      val subcategory = OntologyService.getGoSubCategoryFromAccession(_bedObjectCvTerm.accession);
      subcategory match {
        case GoMolecularFunctionCv.name => GoMolecularFunction;
        case GoBiologicalProcessCv.name => GoBiologicalProcess;
        case GoCellularComponentCv.name => GoCellularComponent;
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
      return _bioObject;
    } else return "";
  }

  def getNXTerminology(): NXTerminology.Value = {

    if (_bedObjectCvTerm.category.equals("Gene Ontology")) {
      val subcategory = OntologyService.getGoSubCategoryFromAccession(_bedObjectCvTerm.accession);
      subcategory match {
        case GoMolecularFunctionCv.name => GoMolecularFunctionCv;
        case GoBiologicalProcessCv.name => GoBiologicalProcessCv;
        case GoCellularComponentCv.name => GoCellularComponentCv;
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

  def isInteraction(): Boolean = {
    return _relation.toLowerCase().contains("binding");
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

}
